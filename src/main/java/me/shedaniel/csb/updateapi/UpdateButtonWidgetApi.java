package me.shedaniel.csb.updateapi;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class UpdateButtonWidgetApi extends ButtonWidget{
    public UpdateButtonWidgetApi(int x, int y, int width, int height, Text message, PressAction onPress/*, NarrationSupplier narrationSupplier*/) {
        super(x,y,width,height,message,onPress,DEFAULT_NARRATION_SUPPLIER);
    }
}
