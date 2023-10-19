package net.csbupdate.gui;

import net.csbupdate.CSBConfig;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import static net.csbupdate.CSBConfig.*;

public class CSBSliderWidget extends SliderWidget {
    
    private final int id;
    
    CSBSliderWidget(int i, int x, int y, float f) {
        super(x, y, 150, 20, Text.empty(), f);
        this.id = i;
        this.setMessage(getDisplayString(i));
    }
    
    @Override
    protected void updateMessage() {
        setMessage(getDisplayString(id));
    }
    
    @Override
    protected void applyValue() {
        updateValue(id);
    }
    
    private void updateValue(int id) {
        switch (id) {
            case 1:
                setRed(getValue());
                break;
            case 2:
                setGreen(getValue());
                break;
            case 3:
                setBlue(getValue());
                break;
            case 4:
                CSBConfig.setAlpha(getValue());
                break;
            case 5:
                setThickness(getValue() * 7);
                break;
            case 7:
                setBlinkAlpha(getValue());
                break;
            case 8:
                setBlinkSpeed(getValue());
        }
    }
    
    private float getValue() {
        return (float) value;
    }
    
    private Text getDisplayString(int id) {
        return switch (id) {
            case 1 -> Text.translatable("gui.csb_update.red" , Math.round(getValue() * 255.0F));
            case 2 -> Text.translatable("gui.csb_update.green" , Math.round(getValue() * 255.0F));
            case 3 -> Text.translatable("gui.csb_update.blue" , Math.round(getValue() * 255.0F));
            case 4 -> Text.translatable("gui.csb_update.outline_alpha" , Math.round(getValue() * 255.0F));
            case 5 -> Text.translatable("gui.csb_update.outline_thickness" , Math.round(getValue() * 7.0F));
            case 7 -> Text.translatable("gui.csb_update.blink_alpha" , Math.round(getValue() * 255.0F));
            case 8 -> Text.translatable("gui.csb_update.blink_speed" , Math.round(getValue() * 100.0F));
            default -> Text.of("Option Error?! (" + id + ")");
        };
    }
    
}