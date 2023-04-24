package fi.arcada.projekt_chi2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Deklarera 4 Button-objekt
    Button btn1, btn2, btn3, btn4, btn6;
    //text view
    TextView displayNumber;
    TextView displayText;

    TextView textViewCol1;
    TextView textViewCol2;
    TextView textViewRow1;
    TextView textViewRow2;

    //SETTINGS
    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;

    // Deklarera 4 heltalsvariabler för knapparnas värden
    double val1, val2, val3, val4;
    double siglvl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Koppla samman Button-objekten med knapparna i layouten
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        //calc button
        btn6 = findViewById(R.id.button6);

        //display text and numbers
        displayNumber = findViewById(R.id.displayNumber);
        displayText = findViewById(R.id.displayText);

        //prefrences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = sharedPref.edit();
        //ändra namn på kolumnerna/raderna
        textViewCol1 = findViewById(R.id.textViewCol1);
        textViewCol2 = findViewById(R.id.textViewCol2);
        textViewRow1 = findViewById(R.id.textViewRow1);
        textViewRow2 = findViewById(R.id.textViewRow2);

        textViewCol1.setText(String.format(sharedPref.getString("col1", "Kolumn 1")));
        textViewCol2.setText(String.format(sharedPref.getString("col2", "Kolumn 2")));

        textViewRow1.setText(String.format(sharedPref.getString("row1", "Rad 2")));
        textViewRow2.setText(String.format(sharedPref.getString("row2", "Rad 2")));
        //signifikansnivå i prefs
        siglvl = Double.parseDouble(sharedPref.getString("sigPref", "0.05"));

    }

    /**
     *  Klickhanterare för knapparna
     */
    public void buttonClick(View view) {

        // Skapa ett Button-objekt genom att type-casta (byta datatyp)
        // på det View-objekt som kommer med knapptrycket
        Button btn = (Button) view;

        // Kontrollera vilken knapp som klickats, öka värde på rätt vaiabel
        if (view.getId() == R.id.button1) val1++;
        if (view.getId() == R.id.button2) val2++;
        if (view.getId() == R.id.button3) val3++;
        if (view.getId() == R.id.button4) val4++;
        /*val1 = 260;     //hardkådade för att devva
        val2 = 450;
        val3 = 350;
        val4 = 700;*/

        // Slutligen, kör metoden som ska räkna ut allt!
        calculate();

    }

    /**
     * Metod som uppdaterar layouten och räknar ut själva analysen.
     */
    public void calculate() {

        // Uppdatera knapparna med de nuvarande värdena
        btn1.setText(String.valueOf(val1));
        btn2.setText(String.valueOf(val2));
        btn3.setText(String.valueOf(val3));
        btn4.setText(String.valueOf(val4));

        // Mata in värdena i Chi-2-uträkningen och ta emot resultatet

        // i en Double-variabel
        double chi2 = Significance.chiSquared(val1, val2, val3, val4);

        // Mata in chi2-resultatet i getP() och ta emot p-värdet
        double pValue = Significance.getP(chi2);

        //procentuella jakande
        double column1pos = (val1 / (val1+val3)*100);
        double column2pos = (val2 / (val2+val4)*100);


        //skriver ut resultat
        displayNumber.setText(String.format("RESULTAT: \n\nChi-2: %.2f\nP-värde: %.2f\nSignifikansnivå: %.2f\n\nVänstra kolumnen positiva svar: %.2f%%\nHögra kolumnen positiva svar: %.2f%%",
                Significance.chiSquared(val1, val2, val3, val4),
                pValue,
                siglvl,
                column1pos,
                column2pos
                ));

        if(pValue > siglvl){

            displayText.setText(String.format("Eftersom p-värdet %.2f > %.2f betyder det att sannolikheten är hög för att nollhypotesen är sann.",
                    pValue,
                    siglvl));
        }
        if(pValue < siglvl){
            displayText.setText(String.format("Eftersom p-värdet %.2f < %.2f betyder det att sannolikheten är låg för att nollhypotesen är sann.",
                    pValue,
                    siglvl));
        }
    }

    public void openSettings (View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }


}