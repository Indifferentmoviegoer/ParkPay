package com.example.parkpay;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;

import static android.content.Intent.getIntent;
import static android.support.v4.media.session.MediaButtonReceiver.handleIntent;

public class FragmentNFC extends Fragment {
    TextView serial;
    NfcAdapter mNfcAdapter;
    PendingIntent mPendingIntent;
    IntentFilter mFilters[];
    Context c;
    String[][] mTechLists;
    private static final String TAG = "myLogs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nfc, null);
        c=getContext();
        serial=(TextView)v.findViewById(R.id.serialNumber);
        return inflater.inflate(R.layout.fragment_nfc,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        processIntent(getActivity().getIntent());
        Log.d(TAG, "1");

    }

    public void processIntent(Intent intent) {
        Log.d(TAG, "2");
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                    NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                Log.d(TAG, "3");
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
                    String typeS = "";
                    switch (type) {
                        case MifareClassic.TYPE_CLASSIC:
                            typeS = "TYPE_CLASSIC";
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
                            + mfc.getBlockCount() + " Block\nStorage Space: " + mfc.getSize() + "\n";
                    for (int j = 0; j < sectorCount; j++) {
                        //Authenticate a sector with key A.
                        auth = mfc.authenticateSectorWithKeyA(j,
                                MifareClassic.KEY_DEFAULT);
                        int bCount;
                        int bIndex;
                        if (auth) {
                            metaInfo += "Sector " + j + ": Verified successfully\n";
                            bCount = mfc.getBlockCountInSector(j);
                            bIndex = mfc.sectorToBlock(j);
                            for (int i = 0; i < bCount; i++) {
                                byte[] data = mfc.readBlock(bIndex);
                                metaInfo += "Block " + bIndex + " : "
                                        + bytesToHexString(data) + "\n";
                                bIndex++;
                            }
                        } else {
                            metaInfo += "Sector " + j + ": Verified failure\n";
                        }
                    }
                    serial.setText(metaInfo);
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
