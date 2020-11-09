import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.converter.ConverterViewModel;
import com.example.converter.R;

import java.util.Objects;


public class ConverterFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    ConverterViewModel converterViewModel;
    EditText textInput;
    EditText textResult;
    Spinner spinnerConvertFrom;
    Spinner spinnerConvertTo;
    String name;
    ClipboardManager clipboard;
    ClipData clipData;

    int fromState = -1, toState = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_converter, container, false);
        spinnerConvertFrom = view.findViewById(R.id.spinner_convert_from);
        spinnerConvertTo = view.findViewById(R.id.spinner_convert_to);

        Bundle arguments = getActivity().getIntent().getExtras();
        if(arguments!=null) {
            name = arguments.get("param").toString();
            int convertFrom = 0, convertTo = 0;
            switch (name) {
                case "currency":
                    convertFrom = convertTo = R.array.currency;
                    break;
                case "length":
                    convertFrom = convertTo = R.array.len;
                    break;
                case "weight":
                    convertFrom = convertTo = R.array.weight;
                    break;
            }
            CreateSpinner(convertFrom, spinnerConvertFrom);
            CreateSpinner(convertTo, spinnerConvertTo);
            setSpinnersState();
        }

        view.findViewById(R.id.switch_input).setOnClickListener(i -> {
            swapSpinners();
        });

        view.findViewById(R.id.copy).setOnClickListener(i -> copyToBuffer(view.findViewById(R.id.edit_text_input)));
        view.findViewById(R.id.put).setOnClickListener(i -> putFromBuffer(view.findViewById(R.id.edit_text_input)));

        converterViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ConverterViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textInput = view.findViewById(R.id.edit_text_input);
        textResult = view.findViewById(R.id.edit_text_result);
        converterViewModel.getDataInput().observe(requireActivity(), i -> textInput.setText(i));
        converterViewModel.getDataResult().observe(requireActivity(), i -> textResult.setText(i));

        if (savedInstanceState != null){
            fromState = Integer.parseInt(savedInstanceState.getString("fromState"));
            toState = Integer.parseInt(savedInstanceState.getString("toState"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("fromState", String.valueOf(spinnerConvertFrom.getSelectedItemPosition()));
        outState.putString("toState", String.valueOf(spinnerConvertTo.getSelectedItemPosition()));

        super.onSaveInstanceState(outState);
    }

    private void swapSpinners(){
        converterViewModel.switchInput();
        int spinnerFromIndex  = spinnerConvertFrom.getSelectedItemPosition();
        spinnerConvertFrom.setSelection(spinnerConvertTo.getSelectedItemPosition());
        spinnerConvertTo.setSelection(spinnerFromIndex);
    }

    private void setConverterFactor(){
        String resourceId = spinnerConvertFrom.getSelectedItem().toString() + spinnerConvertTo.getSelectedItem().toString();
        int converterFactor = getResources().getIdentifier(resourceId, "string", Objects.requireNonNull(getActivity()).getPackageName());
        converterViewModel.changeConverter(Float.parseFloat(getString(converterFactor)));
    }

    private void CreateSpinner(int data, Spinner spinner){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()), data, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setConverterFactor();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    private void copyToBuffer(TextView textComponent){
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        clipData = ClipData.newPlainText("text", textComponent.getText());
        clipboard.setPrimaryClip(clipData);

        Toast toast = Toast.makeText(getActivity().getApplication(), "Text copied", Toast.LENGTH_LONG);
        toast.show();
    }

    private void putFromBuffer(TextView textComponent){
        ClipData data = clipboard.getPrimaryClip();
        ClipData.Item item = data.getItemAt(0);

        String text = item.getText().toString();
        Toast.makeText(getActivity().getApplication(),"Text Pasted ",Toast.LENGTH_SHORT).show();
        ConverterViewModel.addNumber(text);
    }

    private void setSpinnersState(){
        if (fromState > -1){
            spinnerConvertFrom.setSelection(fromState);
            spinnerConvertTo.setSelection(toState);
        }
    }
}
