package com.theleafapps.pro.shopnick.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.theleafapps.pro.shopnick.R;
import com.theleafapps.pro.shopnick.adapters.CartCustomAdapter;
import com.theleafapps.pro.shopnick.dialogs.MyProgressDialog;
import com.theleafapps.pro.shopnick.models.CartItem;
import com.theleafapps.pro.shopnick.models.multiples.CartItems;
import com.theleafapps.pro.shopnick.tasks.GetAllCartItemTask;
import com.theleafapps.pro.shopnick.utils.Commons;
import com.theleafapps.pro.shopnick.utils.Communicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CartActivity extends AppCompatActivity implements Communicator {

    static Context mContext;
    Toolbar toolbar;
    int cart_total = 0;
    CartCustomAdapter cartCustomAdapter;
    RecyclerView cartRecyclerView;
    CartItems cartItems;
    ImageButton continue_shop_btn;
    TextView emptycart_tv, grand_total_value_tv;
    CardView total_card_view;
    FragmentManager fragmentManager;
    ImageButton proceed_payment_button;
    MyProgressDialog myProgressDialog;
    ArrayList<Integer> cart_item_id_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        toolbar = (Toolbar) findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);

        myProgressDialog = new MyProgressDialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_small);

        if (!Commons.hasActiveInternetConnection(this)) {
            Intent intent1 = new Intent(this, NoNetworkActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }

        mContext = CartActivity.this;
        continue_shop_btn = (ImageButton) findViewById(R.id.continue_shopping);
        emptycart_tv = (TextView) findViewById(R.id.emptycart_tv);
        cartRecyclerView = (RecyclerView) findViewById(R.id.cart_recycler_view);
        total_card_view = (CardView) findViewById(R.id.total_cart_value_card_view);
        grand_total_value_tv = (TextView) findViewById(R.id.grand_total_value_tv);
        proceed_payment_button = (ImageButton) findViewById(R.id.proceed_for_payment_button);
        cart_item_id_array = new ArrayList<>();
        cartItems = getCartItems();
        fragmentManager = getFragmentManager();

        if (cartItems != null && cartItems.cartItemList.size() > 0) {
            cart_total = calculate_grand_total(cartItems.cartItemList);
            reloadCartList();
        } else {
            setEmptyCart();
        }

        proceed_payment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putIntegerArrayListExtra("cart_item_id_array", cart_item_id_array);
                intent.putExtra("cart_total", cart_total);
                startActivity(intent);
            }
        });
    }

    private int calculate_grand_total(List<CartItem> cartItemList) {
        int total = 0;
        for (CartItem item : cartItemList) {

            cart_item_id_array.add(item.cart_item_id);
            total += item.product.unit_offerprice * item.quantity;
            total += item.product.unit_shipping;

        }
        grand_total_value_tv.setText("Rs " + total);
        return total;
    }

    private void setEmptyCart() {

        cartRecyclerView.setVisibility(View.GONE);
        total_card_view.setVisibility(View.GONE);
        emptycart_tv.setVisibility(View.VISIBLE);
        continue_shop_btn.setVisibility(View.VISIBLE);

        continue_shop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, ShowcaseActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                MyProgressDialog.show(CartActivity.this, myProgressDialog, "", "");
                startActivity(intent);
                finish();
            }
        });
    }

    private void reloadCartList() {
        cartRecyclerView.setVisibility(View.VISIBLE);
        emptycart_tv.setVisibility(View.GONE);
        total_card_view.setVisibility(View.VISIBLE);
        continue_shop_btn.setVisibility(View.GONE);

        cartCustomAdapter = new CartCustomAdapter(this, cartItems, fragmentManager);
        cartRecyclerView.setAdapter(cartCustomAdapter);

        final LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cartRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    String caller = getIntent().getStringExtra("caller");

                    if (!TextUtils.isEmpty(caller)) {
                        Class callerClass = Class.forName(getPackageName() + ".ui." + caller);
                        Intent intent = new Intent(this, callerClass);
                        if (TextUtils.equals(caller, "ProductListActivity")) {
                            intent.putExtra("subCatId", getIntent().getIntExtra("subCatId", 0));
                            intent.putExtra("categoryId", getIntent().getIntExtra("categoryId", 0));
                        }
                        if (TextUtils.equals(caller, "ProductDetailActivity")) {
                            intent.putExtra("subCatId", getIntent().getIntExtra("subCatId", 0));
                            intent.putExtra("categoryId", getIntent().getIntExtra("categoryId", 0));
                            intent.putExtra("productId", String.valueOf(getIntent().getIntExtra("productId", 0)));
                            intent.putExtra("title", getIntent().getStringExtra("title"));
                        }

                        MyProgressDialog.show(this, myProgressDialog, "", "");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private CartItems getCartItems() {

        try {
            int customer_id = 0;
            SharedPreferences sharedPreferences = getSharedPreferences("Shopnick", Context.MODE_PRIVATE);
            if (!TextUtils.isEmpty(sharedPreferences.getString("cid", ""))) {
                customer_id = Integer.valueOf(sharedPreferences.getString("cid", ""));
            }

            GetAllCartItemTask getAllCartItemTask = new GetAllCartItemTask(this, customer_id);
            getAllCartItemTask.execute().get();

            return getAllCartItemTask.cartItemsReceived;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Tangho", "CartActivity activity >> onRestart Called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Tangho", "CartActivity activity >> onPause Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Tangho", "CartActivity activity >> onDestroy Called");
        myProgressDialog.dismiss();
    }

    @Override
    public void dialogMessage(String msg) {
        cartCustomAdapter.notifyDataSetChanged();
        cartItems = getCartItems();

        if (cartItems.cartItemList.size() > 0) {
            calculate_grand_total(cartItems.cartItemList);
            reloadCartList();
        } else {
            setEmptyCart();
        }
    }
}
