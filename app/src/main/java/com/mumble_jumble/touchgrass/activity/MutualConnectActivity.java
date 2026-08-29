package com.mumble_jumble.touchgrass.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.mumble_jumble.touchgrass.R;
import com.mumble_jumble.touchgrass.data.AuthService;
import com.mumble_jumble.touchgrass.data.FirestoreService;
import com.mumble_jumble.touchgrass.models.Connection;

/**
 * The "mutual connect" task type. Two users meet in person; one shows a QR
 * code (generated here from a Firestore "connections" doc ID), the other
 * scans it. Points are only awarded once BOTH sides have confirmed — never
 * from a one-sided scan/photo of a non-consenting person.
 */
public class MutualConnectActivity extends AppCompatActivity {

    private static final String TAG = "MutualConnect";
    private static final int QR_SIZE_PX = 600;

    private final AuthService authService = new AuthService();
    private final FirestoreService firestoreService = new FirestoreService();

    private String packId;
    private String taskId;
    private int taskPointValue;
    private int totalTasksInPack;

    private ListenerRegistration connectionListener;

    private Button showQrButton;
    private Button scanQrButton;
    private ImageView qrImage;
    private TextView statusText;

    private final ActivityResultLauncher<ScanOptions> scanLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    handleScannedCode(result.getContents());
                }
            });

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    launchScanner();
                } else {
                    Toast.makeText(this, "Camera permission is needed to scan a code", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutual_connect);

        packId = getIntent().getStringExtra("packId");
        taskId = getIntent().getStringExtra("taskId");
        taskPointValue = getIntent().getIntExtra("taskPointValue", 0);
        totalTasksInPack = getIntent().getIntExtra("totalTasksInPack", 1);

        showQrButton = findViewById(R.id.btnShowQr);
        scanQrButton = findViewById(R.id.btnScanQr);
        qrImage = findViewById(R.id.qrImage);
        statusText = findViewById(R.id.connectStatusText);

        showQrButton.setOnClickListener(v -> generateCode());
        scanQrButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                launchScanner();
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }

    private void generateCode() {
        String uid = authService.getCurrentUser() != null ? authService.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "You need to be signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        showQrButton.setEnabled(false);
        scanQrButton.setEnabled(false);
        statusText.setText("Creating your code…");

        Connection connection = new Connection(uid, packId, taskId);
        firestoreService.createConnection(connection, new FirestoreService.ConnectionCreatedCallback() {
            @Override
            public void onSuccess(String connectionDocId) {
                Log.d(TAG, "Created connection: " + connectionDocId);
                showQrBitmap(connectionDocId);
                statusText.setText("Show this to the other hiker — waiting for them to scan…");
                listenForConfirmation(connectionDocId, uid);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to create connection", e);
                showQrButton.setEnabled(true);
                scanQrButton.setEnabled(true);
                statusText.setText("Couldn't create a code — try again");
            }
        });
    }

    private void showQrBitmap(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_SIZE_PX, QR_SIZE_PX);
            Bitmap bitmap = Bitmap.createBitmap(QR_SIZE_PX, QR_SIZE_PX, Bitmap.Config.RGB_565);
            for (int x = 0; x < QR_SIZE_PX; x++) {
                for (int y = 0; y < QR_SIZE_PX; y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qrImage.setImageBitmap(bitmap);
            qrImage.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            Log.e(TAG, "Failed to generate QR bitmap", e);
            Toast.makeText(this, "Couldn't generate the QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private void listenForConfirmation(String connectionId, String myUid) {
        connectionListener = firestoreService.listenToConnection(connectionId, new FirestoreService.ConnectionCallback() {
            @Override
            public void onSuccess(Connection connection) {
                if (connection.bothConfirmed && !connection.pointsAwardedA) {
                    statusText.setText("Connected! Awarding your points…");
                    awardPoints(myUid, connectionId, true);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Connection listener failed", e);
            }
        });
    }

    private void launchScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan your hiking buddy's code");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        scanLauncher.launch(options);
    }

    private void handleScannedCode(String connectionId) {
        String uid = authService.getCurrentUser() != null ? authService.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "You need to be signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        statusText.setText("Checking code…");

        firestoreService.getConnection(connectionId, new FirestoreService.ConnectionCallback() {
            @Override
            public void onSuccess(Connection connection) {
                if (uid.equals(connection.userA)) {
                    statusText.setText("That's your own code — get someone else to scan it!");
                    return;
                }
                if (connection.userB != null) {
                    statusText.setText("This code has already been used.");
                    return;
                }

                firestoreService.confirmConnectionAsUserB(connectionId, uid, packId, taskId,
                        new FirestoreService.WriteCallback() {
                            @Override
                            public void onSuccess() {
                                statusText.setText("Connected! Awarding your points…");
                                awardPoints(uid, connectionId, false);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e(TAG, "Failed to confirm connection", e);
                                statusText.setText("Couldn't confirm — try scanning again");
                            }
                        });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to read scanned connection", e);
                statusText.setText(e.getMessage());
            }
        });
    }

    private void awardPoints(String uid, String connectionId, boolean isUserA) {
        firestoreService.completeTask(uid, packId, taskId, taskPointValue, totalTasksInPack,
                new FirestoreService.WriteCallback() {
                    @Override
                    public void onSuccess() {
                        firestoreService.markConnectionPointsAwarded(connectionId, isUserA,
                                new FirestoreService.WriteCallback() {
                                    @Override
                                    public void onSuccess() {
                                        statusText.setText("+" + taskPointValue + " points! You're all set.");
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e(TAG, "Failed to mark points awarded", e);
                                        statusText.setText("+" + taskPointValue + " points! You're all set.");
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to award points", e);
                        statusText.setText("Connected, but couldn't save points — try again");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionListener != null) {
            connectionListener.remove();
        }
    }
}
