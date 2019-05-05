package GUI_Utils;

import Game.TextCreator;
import Game.Vector3f;
import Game.View;

import java.util.ArrayList;

public class GU_Number {
    ArrayList<GU_Digit> Digits;

    public GU_Number(View view, TextCreator tc, int digitAmount, float height, float z_index, Vector3f translation) {
        this.Digits = new ArrayList<>(digitAmount);

        for (int i = 0; i < digitAmount; i++) {
            this.Digits.add(new GU_Digit(view, tc, height, z_index, translation.add(new Vector3f(i * height * GU_Digit.KERNING, 0, 0))));
        }
    }

    public void pause(View view) {
        this.Digits.forEach(GUDigit -> GUDigit.pause(view));
    }

    public void unPause(View view) {
        this.Digits.forEach(GUDigit -> GUDigit.unPause(view));
    }

    public void setNumber(View view, int number) {
        for (int i = 0; i < this.Digits.size(); i++) {
            int digit = number % (int) Math.pow(10, i + 1);
            number -= digit;
            digit /= Math.pow(10, i);
            this.Digits.get(this.Digits.size() - (i + 1)).setState(view, digit);
        }
    }
}
