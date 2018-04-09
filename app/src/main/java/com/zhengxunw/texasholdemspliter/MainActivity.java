package com.zhengxunw.texasholdemspliter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textin) EditText textIn;
    @BindView(R.id.amount) EditText amount;
    @BindView(R.id.summary) TextView summary;
    @BindView(R.id.container) LinearLayout container;
    @BindView(R.id.add) Button buttonAdd;
    @BindView(R.id.calculation) Button buttonCalculate;

    private Locale locale = Locale.US;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<View> pointsViews = getPointViews();
                List<Integer> points = pointsViews.stream()
                        .map(view -> getPoint(view)).collect(Collectors.toList());
                int totalPoints = points.stream().mapToInt(Integer::intValue).sum();
                float avgPoint = ((float) totalPoints) / pointsViews.size();
                float targetPoint = 2 * avgPoint;
                double totalPointsForSplitting = points.stream()
                        .filter(point -> point < targetPoint)
                        .mapToDouble(point -> targetPoint - point)
                        .sum();
                String totalAmountStr = amount.getText().toString();

                float totalAmount = totalAmountStr.isEmpty() ?
                        0 : Float.valueOf(totalAmountStr);
                pointsViews.forEach(view -> {
                    int point = getPoint(view);
                    float pointToPay = point >= targetPoint ? 0 : targetPoint - point;
                    double portion = pointToPay / totalPointsForSplitting;
                    double portionInPercentage = portion * 100;
                    pointToPay = -pointToPay;
                    TextView resultView = view.findViewById(R.id.result);
                    if (totalAmount > 0) {
                        double singleAmount = totalAmount * portion;
                        resultView.setText(String.format(locale, "%s $%,.2f (%,.2f%%)", pointToPay, singleAmount, portionInPercentage));
                    } else {
                        resultView.setText(String.format(locale, "%s %,.2f%%", pointToPay, portionInPercentage));
                    }
                });

                summary.setText(String.format(locale,
                        "Total number of points is: %s.\n" +
                        "Average number of points is: %,.2f.\n" +
                                "Target number of points is: %,.2f", totalPoints, avgPoint, targetPoint));
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row, null);
                AutoCompleteTextView textOut = addView.findViewById(R.id.textout);
                textOut.setText(textIn.getText().toString());
                Button buttonRemove = addView.findViewById(R.id.remove);
                buttonRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clear();
                        ((LinearLayout) addView.getParent()).removeView(addView);
                    }
                });
                container.addView(addView);
                clear();
            }
        });
    }

    private void clear() {
        textIn.setText("");
        summary.setText("");
    }

    private int getPoint(View view) {
        AutoCompleteTextView childTextView = view.findViewById(R.id.textout);
        String childTextViewValue = childTextView.getText().toString();
        return Integer.valueOf(childTextViewValue);
    }

    private List<View> getPointViews() {
        List<View> result = new ArrayList<>();
        int childCount = container.getChildCount();
        for(int i=0; i<childCount; i++){
            result.add(container.getChildAt(i));
        }
        return result;
    }
}
