package me.shedaniel.csb;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.csb.gui.CSBSettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CSBUpdateModMenuAPI implements ModMenuApi{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory(){
        return CSBSettingsScreen::new;
    }
}
