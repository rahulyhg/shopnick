package com.theleafapps.pro.shopnick.tasks;

import android.content.Context;
import android.util.Log;

import com.theleafapps.pro.shopnick.models.Customer;
import com.theleafapps.pro.shopnick.models.multiples.Customers;
import com.theleafapps.pro.shopnick.utils.AppConstants;
import com.theleafapps.pro.shopnick.utils.PrefUtil;

import org.json.JSONException;

import java.util.HashMap;

import dfapi.ApiException;
import dfapi.ApiInvoker;
import dfapi.BaseAsyncRequest;

/**
 * Created by aviator on 20/07/16.
 */
public class GetCustomerByIdTask extends BaseAsyncRequest {

    public Customers customersRec;
    public Customer customerRec;
    Context context;
    int customerId;

    public GetCustomerByIdTask(Context context, int customerId) {
        this.context = context;
        this.customerId = customerId;
    }

    @Override
    protected void doSetup() throws ApiException, JSONException {
        callerName = "getCustomerById";

        serviceName = AppConstants.DB_SVC;
        endPoint = "customer";
        verb = "GET";

        // filter to only select the contacts in this group
        queryParams = new HashMap<>();
        queryParams.put("filter", "customer_id=" + customerId);

        // request without related would return just {id, contact_group_id, contact_id}
        // set the related field to go get the contact mRecordsList referenced by
        // each contact_group_relationship record
        // queryParams.put("related", "contact_by_contact_id");

        // need to include the API key and session token
        applicationApiKey = AppConstants.API_KEY;
        sessionToken = PrefUtil.getString(context, AppConstants.SESSION_TOKEN);
    }

    @Override
    protected void processResponse(String response) throws ApiException, JSONException {
        //Log.d("Tang Ho"," >>>>> " + response);
        customersRec =
                (Customers) ApiInvoker.deserialize(response, "", Customers.class);
        if (customersRec.customers.size() > 0)
            customerRec = customersRec.customers.get(0);
    }

    @Override
    protected void onCompletion(boolean success) {
        if (success && customersRec != null) {
            Log.d("Tang Ho", " >>>>> Success");
        }
    }

}
