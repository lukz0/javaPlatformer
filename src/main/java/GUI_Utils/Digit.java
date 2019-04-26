package GUI_Utils;

import Game.TextCreator;
import Game.Vector3f;
import Game.View;

public class Digit {
    public Digit(View view, TextCreator tc, int height, float z_index) {
        for (int i = 0; i <= 9; i++) {
            view.createTexturedRectangle(0, height*0.5f, height, 0, z_index, tc.renderString(view, Integer.toString(i)),
                    Vector3f.EMPTY, Vector3f.EMPTY, 0);
        }
    }
}
