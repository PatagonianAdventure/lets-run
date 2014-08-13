package ar.com.gazer.letsrun.Renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by gazer on 8/13/14.
 */
public abstract class Renderer {
    protected void renderSprite(SpriteBatch batch, Body body, Sprite sprite) {
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);

        Vector2 position = body.getPosition();
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        sprite.draw(batch);
    }

    public abstract void render(SpriteBatch batch);
}
