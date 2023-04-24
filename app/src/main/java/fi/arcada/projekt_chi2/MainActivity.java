package fi.arcada.projekt_chi2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;
    // Deklarera 4 Button-objekt
    Button btn1, btn2, btn3, btn4;
    // Column/row names and ID
    DataTableAxis row1, row2, col1, col2;
    DataTableAxis[] tableAxes;
    // Deklarera 4 heltalsvariabler för knapparnas värden
    int val1, val2, val3, val4;

    TextView row1_table, row2_table, col1_table, col2_table, row1_percent, col1_percent, col2_percent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = sharedPref.edit();

        // Koppla samman Button-objekten med knapparna i layouten
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);

        row1 = new DataTableAxis();
        row1.name = sharedPref.getString("row1_name", "Row 1");
        row1.id = R.id.textViewRow1;

        row2 = new DataTableAxis();
        row2.name = sharedPref.getString("row2_name", "Row 2");
        row2.id = R.id.textViewRow2;

        col1 = new DataTableAxis();
        col1.name = sharedPref.getString("col1_name", "Column 1");
        col1.id = R.id.textViewCol1;

        col2 = new DataTableAxis();
        col2.name = sharedPref.getString("col2_name","Column 2");
        col2.id = R.id.textViewCol2;

        tableAxes = new DataTableAxis[]{row1, row2, col1, col2};

        row1_table = findViewById(row1.id);
        row1_table.setText(row1.name);

        row2_table = findViewById(row2.id);
        row2_table.setText(row2.name);

        col1_table = findViewById(col1.id);
        col1_table.setText(col1.name);

        col2_table = findViewById(col2.id);
        col2_table.setText(col2.name);

        row1_percent = findViewById(R.id.percentRow1);
        row1_percent.setText(row1.name);

        col1_percent = findViewById(R.id.percentCol1);
        col1_percent.setText(col1.name);

        col2_percent = findViewById(R.id.percentCol2);
        col2_percent.setText(col2.name);
    }

    /**
     *  Klickhanterare för knapparna
     */
    public void buttonClick(View view) {

        // Skapa ett Button-objekt genom att type-casta (byta datatyp)
        // på det View-objekt som kommer med knapptrycket
        Button btn = (Button) view;

        // Kontrollera vilken knapp som klickats, öka värde på rätt vaiabel
        if (view.getId() == R.id.button1) {
            val1++;
            prefEditor.putInt("val1", val1);
        }
        if (view.getId() == R.id.button2) {
            val2++;
            prefEditor.putInt("val2", val2);
        }
        if (view.getId() == R.id.button3) {
            val3++;
            prefEditor.putInt("val3", val3);
        }
        if (view.getId() == R.id.button4) {
            val4++;
            prefEditor.putInt("val4", val4);
        }

        // Slutligen, kör metoden som ska räkna ut allt!
        calculate();
    }

    public void editDataAxis(View view) {
        // Create local axis object
        DataTableAxis axis = new DataTableAxis();
        axis.id = view.getId();
        final TextView axis_name = (TextView) findViewById(axis.id);
        axis.name = axis_name.getText().toString();

        // Set dialog view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        // Inflate the dialog_edit_text.xml layout file
        View dialogView = inflater.inflate(R.layout.dialog_edit_text, null);
        builder.setView(dialogView);

        final EditText input = dialogView.findViewById(R.id.dialog_edit_text);
        input.setText(axis.name);


        final AlertDialog dialog = builder.create();

        View cancelButton = (Button) dialogView.findViewById(R.id.buttonCancelDialog);
        View saveButton = (Button) dialogView.findViewById(R.id.buttonSaveAxisName);

        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }



        DataTableAxis finalAxis = axis;
        if (saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Update global tableAxes array
                    finalAxis.name = input.getText().toString();
                    for (DataTableAxis tableAx : tableAxes) {
                        if (tableAx.id == finalAxis.id) {
                            tableAx.name = finalAxis.name;

                            updateAxisName();

                            dialog.dismiss();
                            break;
                        }
                    }
                    dialog.dismiss();
                }
            });
        }


        dialog.show();
    }

    public void updateAxisName() {
        row1_table.setText(row1.name);
        row2_table.setText(row2.name);
        col1_table.setText(col1.name);
        col2_table.setText(col2.name);

        row1_percent.setText(row1.name);

        col1_percent.setText(col1.name);
        col2_percent.setText(col2.name);

        prefEditor.putString("row1_name", row1.name);
        prefEditor.putString("row2_name", row2.name);
        prefEditor.putString("col1_name", col1.name);
        prefEditor.putString("col2_name", col2.name);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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

        /**
         *  - Visa chi2 och pValue åt användaren på ett bra och tydligt sätt!
         *
         *  - Visa procentuella andelen jakande svar inom de olika grupperna.
         *    T.ex. (val1 / (val1+val3) * 100) och (val2 / (val2+val4) * 100
         *
         *  - Analysera signifikansen genom att jämföra p-värdet
         *    med signifikansnivån, visa reultatet åt användaren
         *
         */

    }


}