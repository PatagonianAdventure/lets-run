package it.patagonian.android.letsrun.renderers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.patagonian.android.letsrun.game.Car;

/**
 * Created by gazer on 8/13/14.
 */
public class CarRenderer extends Renderer {
    private final Sprite spriteWheel;
    private final Sprite spriteChassis;
    private Car car;

    public CarRenderer(Car car) {
        this.car = car;

        spriteWheel = new Sprite(new Texture("wheel.png"));
        spriteChassis = new Sprite(new Texture("chassis.png"));

        spriteWheel.setSize(car.getWheelRadius()*2, car.getWheelRadius()*2);
        spriteChassis.setSize(car.getWidth(), car.getHeight());
    }

    @Override
    public void render(SpriteBatch batch) {
        renderSprite(batch, car.getLeftWheel(), spriteWheel);
        renderSprite(batch, car.getRightWheel(), spriteWheel);
        renderSprite(batch, car.getChassis(), spriteChassis);
    }
}
