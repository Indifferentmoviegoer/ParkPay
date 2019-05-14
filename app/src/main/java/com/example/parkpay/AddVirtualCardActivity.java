package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Formatter;

public class AddVirtualCardActivity extends AppCompatActivity {

    private ImageView addBack;
    private TextView info;

    private Context c;

    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_virtual_card);

        c=this;

        addBack= findViewById(R.id.addBack);
        info= findViewById(R.id.info);

        addBack.setOnClickListener(v -> {

            Intent intent = new Intent(c,
                    MainActivity.class);
            startActivity(intent);

        });
    }

    @Override
    protected void onNewIntent(Intent pIntent) {
        super.onNewIntent(pIntent);
        setIntent(pIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                    NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                for (String tech : tagFromIntent.getTechList()) {
                    System.out.println(tech);
                }
                boolean auth = false;
                MifareClassic mfc = MifareClassic.get(tagFromIntent);
                try {
                    String metaInfo = "";
                    //Enable I/O operations to the tag from this TagTechnology object.
                    mfc.connect();
                    int type = mfc.getType();
                    int sectorCount = mfc.getSectorCount();
                    String cardID = bytesToHexString(mfc.getTag().getId());
                    String[] cardIdArr = cardID.split(" ");
                    StringBuilder reverseCardId = new StringBuilder();
                    for (String s : cardIdArr) {
                        reverseCardId.append(s);
                    }

                    String typeS = "";
                    switch (type) {
                        case MifareClassic.TYPE_CLASSIC:
                            typeS = "TYPE_CLASSIC";
                            Log.d(TAG, "TYPE_CLASSIC");
                            break;
                        case MifareClassic.TYPE_PLUS:
                            typeS = "TYPE_PLUS";
                            Log.d(TAG, "TYPE_PLUS");
                            break;
                        case MifareClassic.TYPE_PRO:
                            typeS = "TYPE_PRO";
                            Log.d(TAG, "TYPE_PRO");
                            break;
                        case MifareClassic.TYPE_UNKNOWN:
                            typeS = "TYPE_UNKNOWN";
                            Log.d(TAG, "TYPE_UNKNOWN");
                            break;
                    }
                    metaInfo += "Card type：" + typeS + "\n with " + sectorCount + " Sector, "
                            + mfc.getBlockCount() + " Block\nStorage Space: " + mfc.getSize() + "\n"+
                            "Serial number: "+reverseCardId+" "+ Arrays.toString(mfc.getTag().getId());
                    for (int j = 0; j < sectorCount; j++) {
                        //Authenticate a sector with key A.
                        auth = mfc.authenticateSectorWithKeyA(j,
                                MifareClassic.KEY_DEFAULT);
                        int bCount;
                        int bIndex;
//                if (auth) {
//                    metaInfo += "Sector " + j + ": Verified successfully\n";
//                    bCount = mfc.getBlockCountInSector(j);
//                    bIndex = mfc.sectorToBlock(j);
//                    for (int i = 0; i < bCount; i++) {
//                        byte[] data = mfc.readBlock(bIndex);
//                        metaInfo += "Block " + bIndex + " : "
//                                + bytesToHexString(data) + "\n";
//                        bIndex++;
//                    }
//                } else {
//                    metaInfo += "Sector " + j + ": Verified failure\n";
//                }
                    }
                    info.setText(metaInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }
}
