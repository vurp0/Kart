package org.sandholm.max.kart;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by max on 13.12.2015.
 */
public class BaseQuad extends Renderable implements Disposable {
    public float height;

    public float width;

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    //Sprite sprite;

    Vector3 tmp;
    Vector3 tmp2;

    Quaternion rotation;

    public BaseQuad(TextureRegion texture, float height, float width) {
        this.height = height;
        this.width = width;

        tmp = new Vector3();
        tmp2 = new Vector3();
        rotation = new Quaternion();

        material = new Material(
                TextureAttribute.createDiffuse(texture),
                new BlendingAttribute(true, 1f),
                FloatAttribute.createAlphaTest(0.5f)
        );

        /*sprite = new Sprite(texture); //TODO: Fix this, don't use a sprite, actually fix the entire class so it's not just a copy of Xoppa's tutorial
        sprite.setSize(width,  height);

        sprite.setPosition(-sprite.getWidth() * 0.5f, -sprite.getHeight() * 0.5f);

        float [] verts = convert(sprite.getVertices(), sprite.getVertices());
        for (int i = 0; i < verts.length ; i += 8) {
            System.out.println(verts[i] + "    " + verts[i+1] + "    " + verts[i+2] + "    " + verts[i+3] + "    " + verts[i+4] + "    " + verts[i+5] + "    " + verts[i+6] + "    " + verts[i+7]);
        }*/
        float[] vertices = new float[] {
                -width/2,  height/2, 0,   0, 0, 1,   0, 0,
                -width/2, -height/2, 0,   0, 0, 1,   0, 1,
                 width/2, -height/2, 0,   0, 0, 1,   1, 1,
                 width/2,  height/2, 0,   0, 0, 1,   1, 0};/*,

                back[Batch.X1], back[Batch.Y1], 0, 0, 0, -1, back[Batch.U1], back[Batch.V1],
                back[Batch.X2], back[Batch.Y2], 0, 0, 0, -1, back[Batch.U2], back[Batch.V2],
                back[Batch.X3], back[Batch.Y3], 0, 0, 0, -1, back[Batch.U3], back[Batch.V3],
                back[Batch.X4], back[Batch.Y4], 0, 0, 0, -1, back[Batch.U4], back[Batch.V4]
        };*/
        short[] indices = new short[] {0, 1, 2, 2, 3, 0};//, 4, 5, 6, 6, 7, 4 };

        // FIXME: this Mesh needs to be disposed
        meshPart.mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
        meshPart.mesh.setVertices(vertices);
        meshPart.mesh.setIndices(indices);

        meshPart.offset = 0;
        meshPart.size = meshPart.mesh.getNumIndices();
        meshPart.primitiveType = GL20.GL_TRIANGLES;
        meshPart.update();

    }

    public void setDecalRotation(Vector3 dir, Vector3 up) {
        tmp.set(up).crs(dir).nor();
        tmp2.set(dir).crs(tmp).nor();
        rotation.setFromAxes(tmp.x, tmp2.x, dir.x, tmp.y, tmp2.y, dir.y, tmp.z, tmp2.z, dir.z);
        worldTransform.rotate(rotation);
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        material.set(TextureAttribute.createDiffuse(textureRegion));
        //sprite.setRegion(textureRegion);
    }

    private static float[] convert(float[] front, float[] back) {
        return new float[]{
                front[Batch.X2], front[Batch.Y2], 0, 0, 0, 1, front[Batch.U2], front[Batch.V2],
                front[Batch.X1], front[Batch.Y1], 0, 0, 0, 1, front[Batch.U1], front[Batch.V1],
                front[Batch.X4], front[Batch.Y4], 0, 0, 0, 1, front[Batch.U4], front[Batch.V4],
                front[Batch.X3], front[Batch.Y3], 0, 0, 0, 1, front[Batch.U3], front[Batch.V3],

                back[Batch.X1], back[Batch.Y1], 0, 0, 0, -1, back[Batch.U1], back[Batch.V1],
                back[Batch.X2], back[Batch.Y2], 0, 0, 0, -1, back[Batch.U2], back[Batch.V2],
                back[Batch.X3], back[Batch.Y3], 0, 0, 0, -1, back[Batch.U3], back[Batch.V3],
                back[Batch.X4], back[Batch.Y4], 0, 0, 0, -1, back[Batch.U4], back[Batch.V4]
        };
    }

    @Override
    public void dispose() {
        //mesh.dispose();
    }

}
