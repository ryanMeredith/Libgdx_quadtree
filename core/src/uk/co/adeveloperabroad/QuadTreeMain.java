package uk.co.adeveloperabroad;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class QuadTreeMain extends ApplicationAdapter {

	SpriteBatch batch;
	private Stage quadTreeStage;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		OrthographicCamera quadTreeCamera = new OrthographicCamera();
		ScreenViewport quadTreeViewport = new ScreenViewport(quadTreeCamera);
		quadTreeStage = new QuadTreeStage(quadTreeViewport, batch);

	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		quadTreeStage.act();
		quadTreeStage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		quadTreeStage.dispose();
		batch.dispose();
	}
}
