package com.cita.myapplicationadmin.ui.nutrient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplicationadmin.LoginActivity;
import com.cita.myapplicationadmin.R;
import com.cita.myapplicationadmin.adapter.NutrientAdapter;
import com.cita.myapplicationadmin.app.AppController;
import com.cita.myapplicationadmin.model.Nutrient;
import com.cita.myapplicationadmin.utils.Server;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NutrientFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private final static String TAG_ADMIN_ID = "admin_id", TAG = NutrientFragment.class.getSimpleName();
    private static final String URL = Server.URL + "admin/read_nutrient.php", TAG_SUCCESS = "success",
            TAG_NUTRIENT_NAME = "nutrient_name", TAG_CARBOHYDRATE = "carbohydrate", TAG_CALORIES = "calories",
            TAG_FAT = "fat", TAG_PROTEIN = "protein",
            TAG_MESSAGE = "message", TAG_JSON_OBJ = "json_obj_req", TAG_NUTRIENT_ID = "nutrient_id";
    private ArrayList<Nutrient> nutrientArrayList;
    private int adminId;
    private TextView tvEmptyChild;
    private NutrientAdapter nutrientAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_nutrient, container, false);
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LoginActivity.MY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        adminId = sharedPreferences.getInt(TAG_ADMIN_ID, 0);
        tvEmptyChild = root.findViewById(R.id.tv_empty_nutrient);

        getAllNutrient();

        RecyclerView rvChild = root.findViewById(R.id.rv_nutrients);
        nutrientAdapter = new NutrientAdapter(nutrientArrayList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvChild.setLayoutManager(layoutManager);
        rvChild.setAdapter(nutrientAdapter);


        FloatingActionButton fabAddNutrient = root.findViewById(R.id.fab_add_nutrient);
        fabAddNutrient.setSupportBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        fabAddNutrient.setSupportImageTintList(getResources().getColorStateList(R.color.colorWhite));
        fabAddNutrient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NutrientFragmentDirections.ActionNavNutrientToNavCreateNutrient actionNavNutrientToNavCreateNutrient =
                        NutrientFragmentDirections.actionNavNutrientToNavCreateNutrient();
                actionNavNutrientToNavCreateNutrient.setAdminId(adminId);
                Navigation.findNavController(view).navigate(actionNavNutrientToNavCreateNutrient);
            }
        });

        return root;
    }

    private void getAllNutrient() {
        nutrientArrayList = new ArrayList<>();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Read response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt(TAG_SUCCESS);
                            if (success == 1) {
                                JSONArray jsonArrayNutriendId = jsonObject.getJSONArray(TAG_NUTRIENT_ID);
                                JSONArray jsonArrayNutrientName = jsonObject.getJSONArray(TAG_NUTRIENT_NAME);
                                JSONArray jsonArrayCarbohydrate = jsonObject.getJSONArray(TAG_CARBOHYDRATE);
                                JSONArray jsonArrayCalories = jsonObject.getJSONArray(TAG_CALORIES);
                                JSONArray jsonArrayFat = jsonObject.getJSONArray(TAG_FAT);
                                JSONArray jsonArrayProtein = jsonObject.getJSONArray(TAG_PROTEIN);
                                for (int i = 0; i < jsonArrayNutrientName.length(); i++) {
                                    Nutrient nutrient = new Nutrient();
                                    nutrient.setNutrientId(jsonArrayNutriendId.getInt(i));
                                    nutrient.setNutrientName(jsonArrayNutrientName.getString(i));
                                    nutrient.setCarbohydrate(jsonArrayCarbohydrate.getString(i));
                                    nutrient.setCalories(jsonArrayCalories.getString(i));
                                    nutrient.setFat(jsonArrayFat.getString(i));
                                    nutrient.setProtein(jsonArrayProtein.getString(i));
                                    nutrientArrayList.add(nutrient);
                                    nutrientAdapter.setItems(nutrientArrayList);
                                }
                            } else {
                                tvEmptyChild.setText(jsonObject.getString(TAG_MESSAGE));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(LoginActivity.TAG_ADMIN_ID, String.valueOf(adminId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
    }


}
