package edu.sharif.ce.apyugioh.view.model;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Disposable;

public class CardFrontView extends Renderable implements Disposable {

    public CardFrontView(Sprite front) {
        material = new Material(
                TextureAttribute.createDiffuse(front.getTexture()), new BlendingAttribute(false, 1f),
                FloatAttribute.createAlphaTest(0.5f)
        );
        front.setSize(12, 16);
        front.setPosition(-front.getWidth() * 0.5f, -front.getHeight() * 0.5f);
        float[] vertices = convert(front.getVertices());
        short[] indices = new short[]{0, 1, 2, 2, 3, 0};
        meshPart.mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
        meshPart.mesh.setVertices(vertices);
        meshPart.mesh.setIndices(indices);
        meshPart.offset = 0;
        meshPart.size = meshPart.mesh.getNumIndices();
        meshPart.primitiveType = GL20.GL_TRIANGLES;
        meshPart.update();
    }

    private static float[] convert(float[] front) {
        return new float[]{
                front[Batch.X2], front[Batch.Y2], 0, 0, 0, 1, front[Batch.U2], front[Batch.V2],
                front[Batch.X1], front[Batch.Y1], 0, 0, 0, 1, front[Batch.U1], front[Batch.V1],
                front[Batch.X4], front[Batch.Y4], 0, 0, 0, 1, front[Batch.U4], front[Batch.V4],
                front[Batch.X3], front[Batch.Y3], 0, 0, 0, 1, front[Batch.U3], front[Batch.V3],
        };
    }

    @Override
    public void dispose() {
        meshPart.mesh.dispose();
    }
}
