package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;

public class Nfc_reader extends Activity {
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Context context;
    TextView tvNFCContent;
    TextView NumSerie;
    String uuidr="test123";
    TextView Technologies;
    TextView CardType;
    Nfc nfc=new Nfc();
    TextView IsWritable;
    TextView ReadOnly;
    TextView Tail;
    Tag myTag;
    AlertDialog alert;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_reader);
        //readFromIntent(getIntent());

        context = this;
        /* tvNFCContent = (TextView) findViewById(R.id.nfc_content);
        NumSerie = (TextView) findViewById(R.id.NumSerie);
        Technologies = (TextView) findViewById(R.id.Technologies);
        CardType = (TextView) findViewById(R.id.CardType);
        IsWritable = (TextView) findViewById(R.id.IsWritable);
        ReadOnly = (TextView) findViewById(R.id.ReadOnly);
        Tail = (TextView) findViewById(R.id.Tail);*/
       /* this.alert=new AlertDialog.Builder(this)
                .setTitle(" Notification ")
                .setMessage("Plz put the NFC Again  ")
                .setPositiveButton("OK",null)
                .show();*/
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        //IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter NdefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        System.out.println("TagDetect" + tagDetected);
        writeTagFilters = new IntentFilter[]{tagDetected, techDetected, NdefDetected};
        //writeTagFilters = new IntentFilter[] { tagDetected };
        ///////////////
        //////////////

    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            System.out.println("mesg : ----------------- \n"+msgs);
            buildTagViews(msgs);
            System.out.println("Format "+intent.getType());
            System.out.println("data "+intent.getData());

        }
    }
    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String text = "";
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        if(payload.length!=0) {
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            // Get the Text Encoding
            int languageCodeLength = payload[0] & 0063;// Get the Language Code, e.g. "en"
            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

            try {
                // Get the Text
                text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.toString());
            }
        }
        //tvNFCContent.setText("NFC Content: " + text);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        readFromIntent(intent);
        readFromIntent1(intent);
        //this.alert.dismiss();
//        Toast.makeText(this,"action"+intent.getAction(),Toast.LENGTH_SHORT).show();
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareUltralight mu =MifareUltralight.get(myTag);
            try {
                mu.connect();

                byte[] response =mu.transceive(new byte[] {(byte) 0x30,(byte)(0x10 & 0x0FF)});
                /*********************************** Partie format***************************/
              /*  byte[] response2 = mu.transceive(new byte[]{(byte) 0x30, (byte) 0x03});
                if (response2[2] != 6) {

                    try{
                        mu.transceive(new byte[]{
                                (byte) 0xA2,
                                (byte) 0x03,
                                (byte) 0xE1, (byte) 0x10, (byte) 0x06, (byte) 0x00
                        });
                        mu.transceive(new byte[] {
                                (byte)0xA2,
                                (byte)0x04,
                                (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00
                        });
                        mu.transceive(new byte[] {
                                (byte)0xA2,
                                (byte)0x04,
                                (byte)0x03, (byte)0x04, (byte)0xD8, (byte)0x00
                        });
                        mu.transceive(new byte[] {
                                (byte)0xA2,
                                (byte)0x05,
                                (byte)0x00, (byte)0x00, (byte)0xFE, (byte)0x00
                        });
                        //toast(" The card is Formatted.");
                    } catch(Exception e){
                        e.printStackTrace();
                        error("Failed to format the tag.");

                    }


                }*/
/*********************************** Partie format***************************/

                if(response[3]!=4){
                    mu.transceive(new byte[]{
                            (byte) 0xA2,(byte)(0x12 & 0x0FF),
                            (byte)0x0FF, (byte)0x0FF, (byte)0x0FF, (byte)0x0FF});
                    mu.transceive(new byte[] {(byte) 0xA2,(byte)(0x10 & 0x0FF), (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04});
                }
                mu.close();
//
            } catch (IOException e) {
                e.printStackTrace();
            }
            //////////////
            if(!Pattern.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}", this.uuidr)) {
                try {
                    System.out.println("------ write UUID ----");
                    //Toast.makeText(this, "NO UUID Valide" , Toast.LENGTH_LONG);
                    //toast("NO UUID Valid");
                    //System.out.println("------ write UUID1 ----");
                    write(myTag);
                } catch (IOException e) {
                    error(e.getMessage());
                } catch (FormatException e) {
                    error(e.getMessage());
                }

            }

            /////////////////

            String hexdump = new String();
            String hexdump2 = new String();

            String Decidump = new String();
            String prefix = "android.nfc.tech.";
            String[] info = new String[2];
            String[] info2 = new String[2];
            String[] techList = myTag.getTechList();
            String res = "";
            ////////////


            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareUltralight mifare = null;
            int debugCounter = 0;

            byte[] tagId = getIntent().getByteArrayExtra(NfcAdapter.EXTRA_ID);

            NfcA nfca = NfcA.get(myTag);

/////////////////////////
            Short s = nfca.getSak();
            byte[] a = nfca.getAtqa();
            String atqa = new String(a);
            System.out.println("SAK = " + s + "\nATQA = " + a);

//            System.out.println("ATQA" + String.format("%02x", a[0], a[1] ,a[3], a[4]));
            System.out.println("SAK" + String.format("%02x", s));
            for (int i = 0; i < a.length-1; i++) {
                String x = Integer.toHexString(((int) a[i] & 0xff));
                if (x.length() == 1) {
                    x = '0' + x;
                }

                hexdump2 += x + ' ';
            }
            System.out.println("ATQA hex" + hexdump2);

            try {
                nfca.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            for (int i = 0; i < tagId.length; i++) {
                String x = Integer.toHexString(((int) tagId[i] & 0xff));
                if (x.length() == 1) {
                    x = '0' + x;
                }

                hexdump += x + ' ';
            }
            Log.i("EHEHEHEHEHE",hexdump);
            System.out.println("ID ++++++"+hexdump);
            //NumSerie.setText("Numero de serie " + hexdump);
            nfc.setNumero_Serie(hexdump);


            String techListConcat = "Technologies: ";
            for(int i = 0; i < techList.length-1; i++) {
                techListConcat += techList[i].substring(prefix.length()) + ", ";
            }
            techListConcat += techList[techList.length-1].substring(prefix.length());
            info[0] += techListConcat.substring(0, techListConcat.length() - 1) + "\n\n";
//            System.out.println("info ++++++"+techListConcat);
            //Technologies.setText(techListConcat);
            nfc.setTechnologies(techListConcat);
            //Nfc.setUUID(this.uuidr);

            /////////
            info2[0] = "Card Type: ";
            String type = "Unknown";
            for(int i = 0; i < techList.length; i++) {
                if(techList[i].equals(MifareClassic.class.getName())) {
                    info2[1] = "Mifare Classic";
                    MifareClassic mifareClassicTag = MifareClassic.get(myTag);

                    // Type Info
                    switch (mifareClassicTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    info2[0] += "Mifare " + type + "\n";

                    // Size Info
                    info2[0] += "Size: " + mifareClassicTag.getSize() + " bytes \n" ;
                    System.out.println("info size mifare ++++++"+mifareClassicTag.getSize());


                }
                else if(techList[i].equals(MifareUltralight.class.getName())) {
                    info2[1] = "Mifare UltraLight";
                    MifareUltralight mifareUlTag = MifareUltralight.get(myTag);
                    int numberOfPages = 0;
                    // Type Info
                    switch (mifareUlTag.getType()) {
                        case MifareUltralight.TYPE_ULTRALIGHT:
                            type = "Ultralight";
                            numberOfPages = 16;
                            break;
                        case MifareUltralight.TYPE_ULTRALIGHT_C:
                            type = "Ultralight C";
                            numberOfPages = 47;
                            break;
                    }
                    info2[0] += "Mifare " + type;

                    //CardType.setText(info2[0]);
                    nfc.setType_card(info2[0]);
                    System.out.println("info size "+mifareUlTag.getMaxTransceiveLength());

                }

                else if(techList[i].equals(Ndef.class.getName())) {
                    Ndef ndefTag = Ndef.get(myTag);
                    info[0] = "Is Writable: " + ndefTag.isWritable();
                    info[1] ="Read Only: " + ndefTag.canMakeReadOnly() ;
                    System.out.println(info[0]);
                    //IsWritable.setText(info[0]);
                    //ReadOnly.setText(info[1]);
                    //Tail.setText("Tail: "+ndefTag.getMaxSize()+" Bytes")
                }
            }
            System.out.println(Nfc.JsonFormat());
            while(!Nfc.AllValuesSet()){
                try
                {
                    Thread.sleep(1000);

                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }
            finish();
        }
    }
    private void buildTagViews1(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;
        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        if(payload.length!=0){
            System.out.println("payload"+payload.length);
            for (int i=0;i<payload.length;i++) {
                System.out.println("payload"+payload[i]);
            }
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0063;
            try {
                text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.toString());
            }
            System.out.println("texttttt :"+text);
            if(text!=null){
                this.uuidr=text;
            }
            Nfc.setUUID(this.uuidr);
            // toast("UUID"+this.uuidr);

            //toast("The UUID : \n\n"+text);


        }else{
            System.out.println("The card is empty");
        }

    }
    private void readFromIntent1(Intent intent) {
        String action = intent.getAction();
        NdefMessage[] msgs = null;

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
                buildTagViews1(msgs);
                System.out.println(msgs);

            } else {
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{
                        record
                });
                msgs = new NdefMessage[]{
                        msg
                };
                System.out.println("2");
                Toast.makeText(this, "Unknown tag type:" + msgs, Toast.LENGTH_LONG);
            }
            if(rawMsgs==null){
                System.out.println("Failed to Read the tag.");
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0, 0);
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }



    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }
    private void toast(String text) {
        new AlertDialog.Builder(this)
                .setTitle("Result")
                .setMessage(text)
                .setPositiveButton("OK",null)
                .show();
    }
    private void error(String text) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(text)
                .setPositiveButton("OK",null)
                .show();
    }
    /************************************ Write Tag ***********************************/
    UUID uuid =UUID.randomUUID();;
    private void write(Tag myTag) throws IOException, FormatException {
        NdefRecord[] records = {createRecord(uuid.toString())};
        NdefMessage message = new NdefMessage(records);
        NfcA nfca = NfcA.get(myTag);
        System.out.println("nfca"+nfca);
        if (myTag == null) {
            error("Read the card again");
        }
        if (!nfca.isConnected()) {
            System.out.println("not connect");
            nfca.connect();
        }
        System.out.println("yes");
        System.out.println("uuid " + uuid);
        this.uuidr=uuid.toString();

        nfca.transceive(new byte[]{
                (byte) 0x1B,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        });
        nfca.transceive(new byte[]{(byte) 0xA2, (byte) (0x10 & 0x0FF), 0x00, 0x00, (byte) 0x00, (byte) 0xFF});
        nfca.close();
        Ndef ndef=null;
        int size = message.toByteArray().length;
        try {
            System.out.println("1111111111111111111111111111111111");
            if (nfca.isConnected()) {
                System.out.println("close NFCA");
                nfca.close();
            }
            System.out.println("22222222222222222222222222222");
            ndef= Ndef.get(myTag);
            System.out.println("ndef"+ndef);
            if (ndef != null) {
                System.out.println("Open");
                ndef.connect();
                System.out.println("33333333333333333333333333333");
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is read-only.", Toast.LENGTH_LONG).show();
                }
                if (ndef.getMaxSize() < size) {
                    System.out.println("4444444444444444444444444444444444444444");
                    error("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.");
                }
                System.out.println("44444444444444444444444444444444");
                System.out.println(message.toString());
                ndef.writeNdefMessage(message);
                System.out.println("5555555555555555555555555555555555");
                System.out.println(Nfc.JsonFormat());
                Nfc.setUUID(this.uuidr);
                toast("The UUID was successfully written.");
                long id = System.currentTimeMillis();
                ndef.close();
                //Nfc.setUUID();
                System.out.println("6666666666666666666666666666666");
                System.out.println("ClosedNDEF");
            }

        } catch (Exception e) {
            System.out.println("/n ********************************************** /n");
            System.out.println(e.toString());
            error("Failed to write tag nn"+e.toString());
        }

        if(nfca.isConnected()){
            System.out.println("closed1");
            nfca.close();
        }

        NfcA nfca1 = NfcA.get(myTag);
        if(ndef.isConnected()){
            ndef.close();
        }
        /*if(nfca.isConnected()){
            nfca.close();
        }*/

        if(!nfca1.isConnected()) {
            System.out.println("/n NFCA1 *****************");
            nfca1.connect();
            nfca1.transceive(new byte[] {(byte) 0xA2,(byte)(0x12 & 0x0FF),(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
            nfca1.transceive(new byte[] {(byte) 0xA2,(byte)(0x13 & 0x0FF), (byte) 0xFF , (byte) 0xFF , (byte) 0x00, (byte) 0x00});
            nfca1.transceive(new byte[] {(byte) 0xA2,(byte)(0x10 & 0x0FF),  0x00,  0x00, (byte) 0x00, (byte) 0x04});
        }
        System.out.println("/n close NFCA1 *****************");
        nfca1.close();

    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        payload[0] = (byte) langLength;

        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }

    /******************************************************************************************/

}