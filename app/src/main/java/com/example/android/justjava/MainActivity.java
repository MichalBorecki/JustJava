package com.example.android.justjava;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;


/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {

    int quantity = 2;
    int priceOfOneCup = 5;
    int priceOfWhippedCream = 1;
    int priceOfChocolate = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This method is called for hide keyboard when click outside of it
     */
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * This method is called when the increment button is clicked.
     */
    public void increment(View view) {
        if (quantity >= 100) {
            // Show an error message as a toast
            Toast.makeText(this, getString(R.string.too_many_coffees), Toast.LENGTH_SHORT).show();
            // Exit this method early because there is nothing left to do
            return;
        }
        quantity += 1;
        displayQuantity(quantity);
    }

    /**
     * This method is called when the decrement button is clicked.
     */
    public void decrement(View view) {
        if (quantity == 1) {
            // Show an error message as a toast
            Toast.makeText(this, getString(R.string.too_few_coffees), Toast.LENGTH_SHORT).show();
            // Exit this method early because there is nothing left to do
            return;
        }
        quantity -= 1;
        displayQuantity(quantity);
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        // Adding person name to String by EditText field
        EditText nameField = findViewById(R.id.name_field);
        String personName = nameField.getText().toString();
        // Show an error message as a toast when the name has not been entered
        if (personName.trim().length() == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.no_name_toast), Toast.LENGTH_SHORT).show();
            // Exit this method early because user should enter Name first
            return;
        }
        // Figure out if the user wants whipped cream
        CheckBox whippedCreamCheckBox = findViewById(R.id.whipped_cream_check_box);
        boolean hasWhippedCream = whippedCreamCheckBox.isChecked();
        // Figure out if the user wants chocolate topping
        CheckBox chocolateCheckBox = findViewById(R.id.chocolate_check_box);
        boolean hasChocolate = chocolateCheckBox.isChecked();
        int price = calculatePrice(hasWhippedCream, hasChocolate);
        String priceMessage = createOrderSummary(price, personName, hasWhippedCream, hasChocolate);
        // Sending email with order summary
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:orders@bestcoffee.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_topic) + personName);
        intent.putExtra(Intent.EXTRA_TEXT, priceMessage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * displayQuantity
     * This method displays the given quantity value on the screen.
     */
    private void displayQuantity(int numberOfCoffees) {
        TextView quantityTextView = findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + numberOfCoffees);
    }

    /**
     * Calculates the price of the order.
     *
     * @param hasWhippedCream is whether or not the user wants whipped cream topping
     * @param hasChocolate    is whether or not the user wants chocolate topping
     * @return total price
     */
    private int calculatePrice(boolean hasChocolate, boolean hasWhippedCream) {
        if (hasChocolate) {
            priceOfOneCup += priceOfChocolate;
        }
        if (hasWhippedCream) {
            priceOfOneCup += priceOfWhippedCream;
        }
        return (priceOfOneCup * quantity);
    }

    /**
     * Create summary of the order.
     *
     * @param price           of the order
     * @param personName      is a user name entered in EditText view
     * @param hasWhippedCream is whether or not the user wants whipped cream topping
     * @param hasChocolate    is whether or not the user wants chocolate topping
     * @return text summary
     */
    private String createOrderSummary(int price, String personName, boolean hasWhippedCream, boolean hasChocolate) {
        String priceMessage = getString(R.string.order_summary_name, personName);
        priceMessage += "\n" + getString(R.string.order_summary_whipped_cream);
        if (hasWhippedCream) {
            priceMessage += getString(R.string.yes);
        } else {
            priceMessage += getString(R.string.no);
        }
        priceMessage += "\n" + getString(R.string.order_summary_chocolate);
        if (hasChocolate) {
            priceMessage += getString(R.string.yes);
        } else {
            priceMessage += getString(R.string.no);
        }
        priceMessage += "\n" + getString(R.string.order_summary_quantity, quantity);
        priceMessage += "\n" + getString(R.string.order_summary_total, NumberFormat.getCurrencyInstance().format(price));
        priceMessage += "\n" + getString(R.string.thank_you);
        return priceMessage;
    }
}
