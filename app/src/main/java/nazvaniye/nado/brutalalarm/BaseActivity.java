package nazvaniye.nado.brutalalarm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import nazvaniye.nado.brutalalarm.activities.LoginActivity;
import nazvaniye.nado.brutalalarm.activities.MainActivity;
import nazvaniye.nado.brutalalarm.activities.SettingsActivity;

public class BaseActivity extends AppCompatActivity {

    private Drawer sideThing;
    protected BaseActivity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sideThing = initDrawer();
        Alarm.cheerUp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Alarm.cheerUp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Alarm.cheerUp(this);
    }

    public void gamburgerClick(View v) {
        sideThing.openDrawer();
    }

    protected Drawer initDrawer() {
        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.banana);

        int pos = 1;
        if(this.getClass().equals(MainActivity.class)) pos = 1;
        if(this.getClass().equals(LoginActivity.class)) pos = 2;
        if(this.getClass().equals(SettingsActivity.class)) pos = 3;

        SecondaryDrawerItem copyright = new SecondaryDrawerItem().withName(R.string.copyright).withTextColor(Color.WHITE).withSelectedColor(Color.BLACK).withSelectedTextColor(Color.WHITE).withTag("copyright");

        return new DrawerBuilder()
                .withActivity(this)
                .withSliderBackgroundColor(Color.rgb(30, 30, 30))
                .withHeader(img)
                .withCloseOnClick(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.menu).toUpperCase()).withTextColor(Color.RED).withSelectedColor(Color.BLACK).withSelectedTextColor(Color.RED).withTag("menu"),
                        new SecondaryDrawerItem().withName(R.string.social).withTextColor(Color.WHITE).withSelectedColor(Color.BLACK).withSelectedTextColor(Color.WHITE).withTag("social"),
                        new SecondaryDrawerItem().withName(R.string.settings).withTextColor(Color.WHITE).withSelectedColor(Color.BLACK).withSelectedTextColor(Color.WHITE).withTag("settings"),
                        new SecondaryDrawerItem().withName(R.string.about).withTextColor(Color.WHITE).withSelectedColor(Color.BLACK).withSelectedTextColor(Color.WHITE).withTag("about")
                )
                .withSelectedItemByPosition(pos)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((String) drawerItem.getTag()) {
                            case "menu":
                                startActivity(new Intent(context, MainActivity.class));
                                finish();
                                break;
                            case "social":
                                startActivity(new Intent(context, LoginActivity.class));
                                finish();
                                break;
                            case "settings":
                                startActivity(new Intent(context, SettingsActivity.class));
                                finish();
                                break;
                            case "about":
                                Toast.makeText(context, R.string.about_info, Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(context, "WTF?", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                })
                .build();
    }

}
