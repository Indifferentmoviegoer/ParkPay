package com.example.parkpay;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Formatter;

public class Main2Activity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    TextView info;
    NfcAdapter mNfcAdapter;
    PendingIntent mPendingIntent;
    IntentFilter mFilters[];
    String[][] mTechLists;
    Context c;

    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        info=(TextView)findViewById(R.id.info);
        c=this;
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

    public void handleIntent(Intent intent) {
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
            String cardIdArr[] = cardID.split(" ");
            String reverseCardId = "";
            for (int i=0;i < cardIdArr.length;i++)
            {
                reverseCardId += cardIdArr[i];
            }

            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    Log.d(TAG, "TYPE_CLASSIC");
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
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
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

}
