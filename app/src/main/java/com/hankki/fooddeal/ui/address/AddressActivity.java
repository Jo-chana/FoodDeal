package com.hankki.fooddeal.ui.address;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.retrofit.APIClient;
import com.hankki.fooddeal.data.retrofit.APIInterface;
import com.hankki.fooddeal.ui.MainActivity;
import com.hankki.fooddeal.ux.recyclerview.AddressAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class AddressActivity extends AppCompatActivity {
    private APIInterface apiInterface;
    private EditText et_address;
    private TextView tv_search_result;
    private RecyclerView rv_search_result;
    private AddressAdapter addressAdapter;
    private TextWatcher addressWatcher;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        tv_search_result = findViewById(R.id.tv_search_result);
        rv_search_result = (RecyclerView)findViewById(R.id.rv_search_result);
        rv_search_result.addItemDecoration(new DividerItemDecoration(rv_search_result.getContext(), 1));

        apiInterface = APIClient.getKakaoClient().create(APIInterface.class);
        et_address = findViewById(R.id.et_address);

        // 함수를 두 번씩이나 불러오는데 적어도 2글자 이상일 때, 초 넣어서
        addressWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchAddressFromEditText();
            }
            @Override
            public void afterTextChanged(Editable s) {
                searchAddressFromEditText();
            }
        };

        et_address.addTextChangedListener(addressWatcher);
    }

    private void searchAddressFromEditText() {
        ArrayList<String> addressList = new ArrayList<String>();

        disposable = Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Call<ResponseBody> addressSearchCall = apiInterface.getAddress(et_address.getText().toString());
                try {
                    ResponseBody responseBody = addressSearchCall.execute().body();

                    if(responseBody != null) {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONArray jsonArray = jsonObject.getJSONArray("documents");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            addressList.add(jsonObject.getString("address_name"));
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object result) throws Exception {
                        disposable.dispose();
                        rv_search_result.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        addressAdapter = new AddressAdapter(addressList);

                        addressAdapter.setOnItemClickListener(new AddressAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int pos) {
                                Intent intent = new Intent(AddressActivity.this, MainActivity.class);
                                intent.putExtra("Location", et_address.getText().toString());
                                startActivity(intent);
                            }
                        });

                        rv_search_result.setAdapter(addressAdapter);
                        tv_search_result.setText("'" + et_address.getText().toString() + "' 검색 결과");
                    }
                });
    }

}
