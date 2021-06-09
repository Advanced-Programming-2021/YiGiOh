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

public class CardBackView extends Renderable implements Disposable {

    public CardBackView(Sprite back) {
        material = new Material(
                TextureAttribute.createDiffuse(back.getTexture()), new BlendingAttribute(false, 1f),
                FloatAttribute.createAlphaTest(0.5f)
        );
        back.setSize(12, 16);
        back.setPosition(-back.getWidth() * 0.5f, -back.getHeight() * 0.5f);
        float[] vertices = convert(back.getVertices());
        short[] indices = new short[]{0, 1, 2, 2, 3, 0};
        meshPart.mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
        meshPart.mesh.setVertices(vertices);
        meshPart.mesh.setIndices(indices);
        meshPart.offset = 0;
        meshPart.size = meshPart.mesh.getNumIndices();
        meshPart.primitiveType = GL20.GL_TRIANGLES;
        meshPart.update();
    }

    private static float[] convert(float[] back) {
        return new float[]{
                back[Batch.X1], back[Batch.Y1], 0, 0, 0, 1, back[Batch.U1], back[Batch.V1],
                back[Batch.X2], back[Batch.Y2], 0, 0, 0, 1, back[Batch.U2], back[Batch.V2],
                back[Batch.X3], back[Batch.Y3], 0, 0, 0, 1, back[Batch.U3], back[Batch.V3],
                back[Batch.X4], back[Batch.Y4], 0, 0, 0, 1, back[Batch.U4], back[Batch.V4]
        };
    }

    @Override
    public void dispose() {
        meshPart.mesh.dispose();
    }

}
