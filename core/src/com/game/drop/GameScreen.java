package com.game.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
	private final Drop game;
	private OrthographicCamera camera;
	private Texture imgDroplet;
	private Texture imgBucket;
	private Sound sfxDrop;
	private Music bgmRain;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private int dropsGathered;
	private long lastTime;
	private int fps;
	private int lastFps;
	private final Vector3 touchPos = new Vector3();

	public GameScreen(Drop game) {
		this.game = game;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		imgDroplet = new Texture("droplet.png");
		imgBucket = new Texture("bucket.png");

		sfxDrop = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		bgmRain = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		bgmRain.setLooping(true);
		bgmRain.setVolume(0.1F);

		bucket = new Rectangle(camera.viewportWidth / 2 - 64 / 2, 20, 64, 64);

		raindrops = new Array<>();
		spawnRaindrop();
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2F, 1);

		camera.update();

		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 5, 475);
		game.font.draw(game.batch, "FPS: " + lastFps, camera.viewportWidth - 60, 475);
		game.batch.draw(imgBucket, bucket.x, bucket.y, bucket.width, bucket.height);
		for (Rectangle raindrop : raindrops) {
			game.batch.draw(imgDroplet, raindrop.x, raindrop.y);
		}
		game.batch.end();

		fps++;
		if (TimeUtils.millis() - lastTime > 1000) {
			lastFps = fps;
			fps = 0;
			lastTime = TimeUtils.millis();
		}

		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - bucket.width / 2;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			bucket.x -= 350 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			bucket.x += 350 * Gdx.graphics.getDeltaTime();

		if (bucket.x < 0)
			bucket.x = 0;
		if (bucket.x > camera.viewportWidth - bucket.width)
			bucket.x = camera.viewportWidth - bucket.width;

		if (TimeUtils.millis() - lastDropTime > 2500)
			spawnRaindrop();

		for (Rectangle raindrop : raindrops) {
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();

			if (raindrop.y + raindrop.height < 0)
				raindrops.removeValue(raindrop, false);

			if (raindrop.overlaps(bucket)) {
				dropsGathered++;
				sfxDrop.play();
				raindrops.removeValue(raindrop, false);
			}
		}
	}

	@Override
	public void show() {
		bgmRain.play();
	}

	@Override
	public void dispose() {
		imgBucket.dispose();
		imgDroplet.dispose();
		bgmRain.dispose();
		sfxDrop.dispose();
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle(
				MathUtils.random(0, camera.viewportWidth - 64),
				480, 64, 64
		);

		raindrops.add(raindrop);
		lastDropTime = TimeUtils.millis();

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}
}
