package TeamiumPremium;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

import ArmyOfBaab.*;
import ProjectOneEngine.*;

// This will allow us to test our code in the AIGameApp environment without directly
// modifying it.
public class CustomGameApp extends AIGameApp {

    public void start(Stage primaryStage) {
        // Initialize the AIGameApp to its default values
        super.start(primaryStage);

        // This "injects" the desired players into the AIGameApp, bypassing accessor modifiers.
        try {
            // Access modifiers are meaingless when faced with my stupidity. Tremble in fear!

            // Set the players.
            Field defaultAccessibilityTOP_Player = AIGameApp.class.getDeclaredField("TOP_Player");
            defaultAccessibilityTOP_Player.setAccessible(true);
            defaultAccessibilityTOP_Player.set(this, new CarefulBaab());

            Field defaultAccessibilityBOT_Player = AIGameApp.class.getDeclaredField("BOT_Player");
            defaultAccessibilityBOT_Player.setAccessible(true);
            defaultAccessibilityBOT_Player.set(this, new PruniumWiserOlderBaabPremium(13));

            // Set the names for the (new) players.
            Player TOP_Player = (Player) defaultAccessibilityTOP_Player.get(this);
            Player BOT_Player = (Player) defaultAccessibilityBOT_Player.get(this);

            String nameTop;
            String nameBot;
            if (TOP_Player != null) {
                nameTop = TOP_Player.getPlayName();
            } else {
                nameTop = "Human Player";
            }
            if (BOT_Player != null) {
                nameBot = BOT_Player.getPlayName();
            } else {
                nameBot = "Human Player";
            }
            String title = "TOP: " + nameTop + " ";
            title = title + "  vs  BOT: " + nameBot;
            primaryStage.setTitle(title);

            // Set the state for the (new) players and names.
            Field defaultAccessibilityState = AIGameApp.class.getDeclaredField("state");
            defaultAccessibilityState.setAccessible(true);
            defaultAccessibilityState.set(this, new GameState(nameTop, nameBot));

            // Update the JavaFX canvas to reflect (good pun) the changes.
            Field defaultAccessibilityTest_canvas = AIGameApp.class.getDeclaredField("test_canvas");
            defaultAccessibilityTest_canvas.setAccessible(true);
            Class defaultAccessibilityGDG = Class.forName("ProjectOneEngine.GameDisplayGraphics");
            Class[] displayStateArgs = new Class[]{Canvas.class, GameState.class};
            Method methodInDefaultAcessibilityClassDisplayState = defaultAccessibilityGDG.getDeclaredMethod("displayState", displayStateArgs);
            methodInDefaultAcessibilityClassDisplayState.setAccessible(true);
            methodInDefaultAcessibilityClassDisplayState.invoke(null, defaultAccessibilityTest_canvas.get(this), defaultAccessibilityState.get(this));
        } catch (Exception e) {
            // Java requires exceptions to be caught when using reflection.
            throw new IllegalArgumentException(e.toString());
        }
    }
}
