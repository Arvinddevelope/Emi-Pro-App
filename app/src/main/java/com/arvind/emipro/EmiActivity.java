package com.arvind.emipro;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;

public class EmiActivity extends AppCompatActivity {

    private EditText editTextLoanAmount, editTextInterestRate, editTextLoanTenure;
    private Button buttonCalculate, buttonReset, buttonGeneratePDF;
    private TextView textViewEMIResult, textViewTotalInterest, textViewTotalPayment, textViewPaymentSchedule;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi);

        editTextLoanAmount = findViewById(R.id.editTextLoanAmount);
        editTextInterestRate = findViewById(R.id.editTextInterestRate);
        editTextLoanTenure = findViewById(R.id.editTextLoanTenure);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonReset = findViewById(R.id.buttonReset);
        buttonGeneratePDF = findViewById(R.id.buttonGeneratePDF);
        textViewEMIResult = findViewById(R.id.textViewEMIResult);
        textViewTotalInterest = findViewById(R.id.textViewTotalInterest);
        textViewTotalPayment = findViewById(R.id.textViewTotalPayment);
        textViewPaymentSchedule = findViewById(R.id.textViewPaymentSchedule);

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateEMI();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });

        buttonGeneratePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EmiActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(EmiActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    // Permission has already been granted
                    generatePDF();
                }
            }
        });
    }

    private void calculateEMI() {
        String loanAmountStr = editTextLoanAmount.getText().toString();
        String interestRateStr = editTextInterestRate.getText().toString();
        String loanTenureStr = editTextLoanTenure.getText().toString();

        if (TextUtils.isEmpty(loanAmountStr) || TextUtils.isEmpty(interestRateStr) || TextUtils.isEmpty(loanTenureStr)) {
            Toast.makeText(this, "Please enter all the values", Toast.LENGTH_SHORT).show();
            return;
        }

        double loanAmount = Double.parseDouble(loanAmountStr);
        double interestRate = Double.parseDouble(interestRateStr) / 12 / 100;
        double loanTenure = Double.parseDouble(loanTenureStr) * 12;

        double emi = (loanAmount * interestRate * Math.pow(1 + interestRate, loanTenure)) / (Math.pow(1 + interestRate, loanTenure) - 1);
        double totalPayment = emi * loanTenure;
        double totalInterest = totalPayment - loanAmount;

        textViewEMIResult.setText(String.format("Monthly EMI: %.2f", emi));
        textViewTotalInterest.setText(String.format("Total Interest: %.2f", totalInterest));
        textViewTotalPayment.setText(String.format("Total Payment: %.2f", totalPayment));
    }

    private void resetFields() {
        editTextLoanAmount.setText("");
        editTextInterestRate.setText("");
        editTextLoanTenure.setText("");
        textViewEMIResult.setText("");
        textViewTotalInterest.setText("");
        textViewTotalPayment.setText("");
        textViewPaymentSchedule.setText("");
    }

    private void generatePDF() {
        String fileName = "EMI_Details.pdf";
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + fileName;

        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Retrieve text from TextViews
            String emiText = textViewEMIResult.getText().toString();
            String totalInterestText = textViewTotalInterest.getText().toString();
            String totalPaymentText = textViewTotalPayment.getText().toString();
            String paymentScheduleText = textViewPaymentSchedule.getText().toString();

            // Add content to PDF
            document.add(new Paragraph("EMI Calculator Results"));
            document.add(new Paragraph("Monthly EMI: " + emiText));
            document.add(new Paragraph("Total Interest: " + totalInterestText));
            document.add(new Paragraph("Total Payment: " + totalPaymentText));
            document.add(new Paragraph("Payment Schedule: " + paymentScheduleText));

            document.close();
            Toast.makeText(this, "PDF Generated: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                generatePDF();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission Denied! Cannot generate PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
