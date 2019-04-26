package GUI_Utils;

import Game.TextCreator;
import Game.Vector3f;
import Game.View;

import java.util.ArrayList;

public class Number {
    ArrayList<Digit> digits;
    Number(View view, TextCreator tc , int digitAmount, float height, float z_index, Vector3f translation) {
        this.digits = new ArrayList<>(digitAmount);

        for (int i = 0; i < digitAmount; i++) {
            this.digits.add(new Digit(view, tc, height, z_index, translation.add(new Vector3f(i*height/2, 0, 0))));
        }
    }

    public void pause(View view) {
        this.digits.forEach(digit -> digit.pause(view));
    }

    public void unPause(View view) {
        this.digits.forEach(digit -> digit.unPause(view));
    }
}
