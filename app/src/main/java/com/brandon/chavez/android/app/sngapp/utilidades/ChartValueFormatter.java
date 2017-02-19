package com.brandon.chavez.android.app.sngapp.utilidades;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

public class ChartValueFormatter implements AxisValueFormatter {

    private String[] mValues;

    public ChartValueFormatter(String[] values) {
        this.mValues = values;
    }

    public String getFormattedValue(float value, AxisBase axis) {
        return mValues[(int) value];
    }

    public int getDecimalDigits() { return 0; }
}
