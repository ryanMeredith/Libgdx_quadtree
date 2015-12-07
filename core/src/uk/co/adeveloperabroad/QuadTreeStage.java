package uk.co.adeveloperabroad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class QuadTreeStage extends Stage {

    private Batch batch;
    private Viewport viewport;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();;

    private QuadTree quadTree;
    private float quadTreeWidth;
    private float quadTreeHeight;
    private float quadX = 10.0f;
    private float quadY = 10.0f;

    private int numberParticles = 8;
    private float particleSize = 20.0f;
    private Array<Particle> particles = new Array<Particle>(numberParticles);;
    private Array<Particle> returnObjects = new Array<Particle>();

    private BitmapFont distanceFont;
    private ShaderProgram fontShader;
    GlyphLayout layout = new GlyphLayout();


    public QuadTreeStage(Viewport viewport, Batch batch) {
        this.batch = batch;
        this.viewport = viewport;

        setQuadTreeSize();
        for (int i=0; i < numberParticles; i++) {
            float randomX = MathUtils.random(quadX, quadX + quadTreeWidth);
            float randomY = MathUtils.random(quadY, quadY + quadTreeHeight);
            particles.add( new Particle(new Rectangle(randomX,randomY, particleSize, particleSize),i));
        }

        setupFonts();
    }

    @Override
    public void draw() {

        //fresh tree
        quadTree.clear();
        // particles in new position
        moveParticles();
        // insert all particles into the tree
        insertParticles();
        // see which ones collided
        collisionCheck();
        // draw tree includes particles
        drawQuadTree(quadTree);
        // add number on top of particles to make them easy to track
        numberParticles();
        super.draw();
    }

    @Override
    public void dispose() {
        distanceFont.dispose();
        fontShader.dispose();
        shapeRenderer.dispose();
        batch.dispose();
        super.dispose();
    }

    private void drawParticle(Particle particle) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(particle.getColour());
        shapeRenderer.rect(particle.getX(), particle.getY(),
                particle.getWidth(), particle.getHeight());
        shapeRenderer.end();
    }

    private void drawQuadrant(float x, float y, float width, float height) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
    }

    private void drawQuadTree(QuadTree node)
    {
        if (node.nodes != null){
            for (int i=0; i < node.nodes.length; i++)
            {
                if (node.nodes[i] != null)
                    drawQuadTree(node.nodes[i]);
            }
            drawQuadrant(node.bounds.getX(), node.bounds.getY(), node.bounds.getWidth(), node.bounds.getHeight());
        }

        if (node.objects != null) {
            for (Particle particle : node.objects) {
                drawParticle(particle);
            }
        }
    }

    private void moveParticles() {

        for(Particle particle : particles) {

            if (isAtEdge(particle)) {
                particle.changeDirection();
            }

            particle.setX(MathUtils.clamp(particle.getX() + particle.velocityX * Gdx.graphics.getDeltaTime(),
                    quadX,
                    quadX + quadTreeWidth - particle.getWidth())
            );
            particle.setY(MathUtils.clamp(particle.getY() + particle.velocityY * Gdx.graphics.getDeltaTime(),
                    quadY,
                    quadY + quadTreeHeight - particle.getHeight()));
        }
    }

    private void insertParticles() {
        for (Particle particle : particles) {
            quadTree.insert(particle);
        }
    }

    private void collisionCheck() {

        for (Particle particle: particles) {
            particle.collided = false;
            returnObjects.clear();
            returnObjects = quadTree.retrieve(returnObjects, particle);

            for (Particle returnParticle : returnObjects)
            {
                if (particle != returnParticle)
                {
                    displayCheckingInfo(particle.id, returnParticle.id);

                    if (hasCollided(particle, returnParticle))
                    {
                        particle.collided = true;
                    }
                }
            }
        }
    }

    private void displayCheckingInfo(int particleId, int returnParticleId) {

        float leftOfQuad = quadTreeWidth + quadX + 20.0f;
        float topOfQuad = quadTreeHeight + quadY;
        float fontHeight = 20.0f;
        float fontWidth = 15.0f;

        batch.begin();

        for (int i = 0; i < numberParticles; ++i) {
            if (particleId == i) {
                layout.setText(distanceFont, Integer.toString(particleId) + " : ");
                distanceFont.draw(batch,
                        layout,
                        leftOfQuad,
                        topOfQuad - (particleId * fontHeight)
                );
                distanceFont.draw(batch,
                        Integer.toString(returnParticleId),
                        leftOfQuad + layout.width + 10.0f + (fontWidth * returnParticleId),
                        topOfQuad - (particleId * fontHeight)
                );
            }
        }
        batch.end();
    }

    private void numberParticles() {

        batch.begin();
        for (Particle particle : particles) {

            distanceFont.draw(batch,
                    Integer.toString(particle.id),
                    particle.getX() + particle.getWidth() * 0.2f,
                    particle.getY() - 5.0f);
        }
        batch.end();
    }


    private boolean hasCollided(Particle particle, Particle returnParticle) {
        Boolean hasCollided = true;

        float particleMinX = particle.getX();
        float particleMinY = particle.getY();
        float particleMaxX = particle.getX() + particle.getWidth();
        float particleMaxY = particle.getY() + particle.getHeight();

        float returnParticleMinX = returnParticle.getX();
        float returnParticleMinY = returnParticle.getY();
        float returnParticleMaxX = returnParticle.getX() + returnParticle.getWidth();
        float returnParticleMaxY = returnParticle.getY() + returnParticle.getHeight();

        if (
                (returnParticleMinX > particleMaxX || returnParticleMaxX < particleMinX)
                || (returnParticleMinY > particleMaxY || returnParticleMaxY < particleMinY)
        ) {
            hasCollided = false;
        }
        return hasCollided;
    }

    private boolean isAtEdge(Particle particle) {
        Boolean atEdge = false;
        if (particle.getX() + particle.getWidth() >= quadX + quadTreeWidth
                || particle.getX() <= quadX
                || particle.getY()  <= quadY
                || particle.getY() + particle.getHeight()  >= quadY + quadTreeHeight) {
            atEdge = true;
        }
        return atEdge;
    }

    private void setupFonts() {

        distanceFont = new BitmapFont(Gdx.files.internal("fonts/oswald-distance.fnt"));
        distanceFont.setColor(Color.WHITE);
        distanceFont.getData().setScale(0.4f);
        fontShader = new ShaderProgram(Gdx.files.internal("fonts/font.vert"),
                Gdx.files.internal("fonts/font.frag"));

        if (!fontShader.isCompiled()) {
            Gdx.app.error("Shader Sample",
                    "Shader compilation failed:\n" + fontShader.getLog());
        }

    }

    private void setQuadTreeSize() {
        quadTreeWidth = Gdx.graphics.getWidth() * 0.70f;
        quadTreeHeight = Gdx.graphics.getHeight() * 0.90f;
        Rectangle quadTreeSize = new Rectangle(quadX,quadY,quadTreeWidth, quadTreeHeight);
        quadTree = new QuadTree(1, quadTreeSize);
    }

}
