package test.uk.co.adeveloperabroad;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import uk.co.adeveloperabroad.QuadTree;

public class QuadTreeTest {

    QuadTree quadTree;

    // create new quadtree for every test.
    @Before
    public void setUp() {
        Rectangle quadTreeSize = new Rectangle(0.0f, 0.0f, 100.0f, 100.0f);
        quadTree = new QuadTree(1, quadTreeSize);
        // set to split at 2
        quadTree.MAX_OBJECTS = 2;
    }


    @Test
    public void testQuadtreeSetup() {
        Rectangle bounds = quadTree.bounds;
       // System.out.println(bounds.getHeight() + bounds.getWidth());
        assertEquals(100.0f, bounds.getWidth(), 0);
        assertEquals(100.0f, bounds.getHeight(), 0);

        //level 1
        assertEquals(1, quadTree.level);

        QuadTree[] internalNodes = quadTree.nodes;
        assertEquals(4, internalNodes.length);
        // space for nodes but not initialised
        for (QuadTree node: internalNodes) {
            assertNull(node);
        }

    }

    @Test
    public void testInsertSingleObject() {
        // bottom left 20 x 20
        Rectangle object = new Rectangle(0.0f, 0.0f, 20.0f, 20.0f);
        quadTree.insert(object);

        Array<Rectangle> insertedObjects = quadTree.objects;
        assertEquals(1, insertedObjects.size);
        assertEquals(20.0f, insertedObjects.get(0).getWidth(), 0);
        assertEquals(20.0f, insertedObjects.get(0).getHeight(), 0);
        assertEquals(0.0f, insertedObjects.get(0).getX(), 0);
        assertEquals(0.0f, insertedObjects.get(0).getY(), 0);

        Array<Rectangle> returnObjects = new Array<Rectangle>();
        returnObjects = quadTree.retrieve(returnObjects, object);

        // the quadtree always returns itself
        assertEquals(1, returnObjects.size);
        assertEquals(20.0f, returnObjects.get(0).getWidth(), 0);
        assertEquals(20.0f, returnObjects.get(0).getHeight(), 0);
        assertEquals(0.0f, returnObjects.get(0).getX(), 0);
        assertEquals(0.0f, returnObjects.get(0).getY(), 0);

        // quadtree contains one object
        assertEquals(1, quadTree.objects.size);

        QuadTree[] internalNodes = quadTree.nodes;
        assertEquals(4, internalNodes.length);
        // space for nodes but not initialised as will not split unless two in the area
        for (QuadTree node: internalNodes) {
            assertNull(node);
        }
    }

    @Test
    public void testInsertTwoObjectsNoCollision() {

        Rectangle object = new Rectangle(0.0f, 0.0f, 20.0f, 20.0f);
        quadTree.insert(object);

        // top right corner
        Rectangle object2 = new Rectangle(80.0f, 80.0f, 20.0f, 20.0f);
        quadTree.insert(object2);

        Array<Rectangle> insertedObjects = quadTree.objects;
        assertEquals(2, insertedObjects.size);
        assertEquals(object, insertedObjects.get(0));
        assertEquals(object2, insertedObjects.get(1));

        Array<Rectangle> returnObjects = new Array<Rectangle>();
        assertEquals(0, returnObjects.size);
        // get collisions with object
        returnObjects = quadTree.retrieve(returnObjects, object);
        assertEquals(2, returnObjects.size);
        assertEquals(object, returnObjects.get(0));
        assertEquals(object2, returnObjects.get(1));

        // return objects should be cleared on each run.
        returnObjects.clear();
        assertEquals(0, returnObjects.size);
        returnObjects = quadTree.retrieve(returnObjects, object2);
        assertEquals(2, returnObjects.size);
        assertEquals(object, returnObjects.get(0));
        assertEquals(object2, returnObjects.get(1));

        // quadtree contains two object
        assertEquals(2, quadTree.objects.size);

        QuadTree[] internalNodes = quadTree.nodes;
        assertEquals(4, internalNodes.length);
        // space for nodes but not initialised as will not split unless two in the area
        for (QuadTree node: internalNodes) {
            assertNull(node);
        }
    }

    @Test
    public void TestInsertThreeObjects() {

        // bottom left corner
        Rectangle object = new Rectangle(0.0f, 0.0f, 20.0f, 20.0f);
        quadTree.insert(object);

        // top right corner
        Rectangle object2 = new Rectangle(80.0f, 80.0f, 20.0f, 20.0f);
        quadTree.insert(object2);

        // bottom right corner
        Rectangle object3 = new Rectangle(80.0f, 0.0f, 20.0f, 20.0f);
        quadTree.insert(object3);

        Array<Rectangle> returnObjects = new Array<Rectangle>();
        // get collisions with object
        returnObjects = quadTree.retrieve(returnObjects, object);
        // only check itself
        assertEquals(1, returnObjects.size);
        assertEquals(object, returnObjects.get(0));

        returnObjects.clear();
        returnObjects = quadTree.retrieve(returnObjects, object2);
        assertEquals(1, returnObjects.size);
        assertEquals(object2, returnObjects.get(0));

        returnObjects.clear();
        returnObjects = quadTree.retrieve(returnObjects, object3);
        assertEquals(1, returnObjects.size);
        assertEquals(object3, returnObjects.get(0));

        // quadtree has split
        QuadTree[] internalNodes = quadTree.nodes;
        assertEquals(4, internalNodes.length);
        assertEquals(2, internalNodes[0].level);
        assertEquals(2, internalNodes[1].level);
        assertEquals(2, internalNodes[2].level);
        assertEquals(2, internalNodes[3].level);

        // node 0 bottom right
        assertEquals(50.0f, internalNodes[0].bounds.getX(), 0);
        assertEquals(0.0f, internalNodes[0].bounds.getY(), 0);
        assertEquals(50.0f, internalNodes[0].bounds.getWidth(), 0);
        assertEquals(50.0f, internalNodes[0].bounds.getHeight(), 0);

        // node 1 bottom left
        assertEquals(0.0f, internalNodes[1].bounds.getX(), 0);
        assertEquals(0.0f, internalNodes[1].bounds.getY(), 0);
        assertEquals(50.0f, internalNodes[1].bounds.getWidth(), 0);
        assertEquals(50.0f, internalNodes[1].bounds.getHeight(), 0);

        // node 2 top left
        assertEquals(0.0f, internalNodes[2].bounds.getX(), 0);
        assertEquals(50.0f, internalNodes[2].bounds.getY(), 0);
        assertEquals(50.0f, internalNodes[2].bounds.getWidth(), 0);
        assertEquals(50.0f, internalNodes[2].bounds.getHeight(), 0);

        // node 3 top right
        assertEquals(50.0f, internalNodes[3].bounds.getX(), 0);
        assertEquals(50.0f, internalNodes[3].bounds.getY(), 0);
        assertEquals(50.0f, internalNodes[3].bounds.getWidth(), 0);
        assertEquals(50.0f, internalNodes[3].bounds.getHeight(), 0);

        // quadtree contains 0 object
        assertEquals(0, quadTree.objects.size);

        //one object in each node except top left, node 2
        assertEquals(1, internalNodes[0].objects.size);
        assertEquals(object3, internalNodes[0].objects.get(0));

        assertEquals(1, internalNodes[1].objects.size);
        assertEquals(object, internalNodes[1].objects.get(0));

        assertEquals(0, internalNodes[2].objects.size);

        assertEquals(1, internalNodes[3].objects.size);
        assertEquals(object2, internalNodes[3].objects.get(0));

    }

    @Test
    public void TestInsertThreeObjectsAndCentralObject() {

        // bottom left corner
        Rectangle object = new Rectangle(0.0f, 0.0f, 20.0f, 20.0f);
        quadTree.insert(object);

        // top right corner
        Rectangle object2 = new Rectangle(80.0f, 80.0f, 20.0f, 20.0f);
        quadTree.insert(object2);

        // bottom right corner
        Rectangle object3 = new Rectangle(80.0f, 0.0f, 20.0f, 20.0f);
        quadTree.insert(object3);

        //center object
        Rectangle object4 = new Rectangle(40.0f, 40.0f, 10.0f, 20.0f);
        quadTree.insert(object4);

        Array<Rectangle> returnObjects = new Array<Rectangle>();
        // get collisions with object
        returnObjects = quadTree.retrieve(returnObjects, object);
        // only check itself
        assertEquals(2, returnObjects.size);
        assertEquals(object, returnObjects.get(0));


        returnObjects.clear();
        returnObjects = quadTree.retrieve(returnObjects, object2);
        assertEquals(2, returnObjects.size);
        assertEquals(object2, returnObjects.get(0));
        assertEquals(object4, returnObjects.get(1));

        returnObjects.clear();
        returnObjects = quadTree.retrieve(returnObjects, object3);
        assertEquals(2, returnObjects.size);
        assertEquals(object3, returnObjects.get(0));
        assertEquals(object4, returnObjects.get(1));

        returnObjects.clear();
        returnObjects = quadTree.retrieve(returnObjects, object4);
        assertEquals(4, returnObjects.size);
        assertEquals(object3, returnObjects.get(0));
        assertEquals(object, returnObjects.get(1));
        assertEquals(object2, returnObjects.get(2));
        assertEquals(object4, returnObjects.get(3));

        // quadtree has split
        QuadTree[] internalNodes = quadTree.nodes;
        assertEquals(4, internalNodes.length);

        // quadtree root contains object4
        assertEquals(1, quadTree.objects.size);

        //one object in each node except top left, node 2
        assertEquals(1, internalNodes[0].objects.size);
        assertEquals(object3, internalNodes[0].objects.get(0));

        assertEquals(1, internalNodes[1].objects.size);
        assertEquals(object, internalNodes[1].objects.get(0));

        assertEquals(0, internalNodes[2].objects.size);

        assertEquals(1, internalNodes[3].objects.size);
        assertEquals(object2, internalNodes[3].objects.get(0));

    }

    @Test
    public void TestInsertThreeObjectsBottomLeftAndCenter(){

        // all in bottom left hand corner in subquad node 1
        // bottom left corner
        Rectangle object = new Rectangle(0.0f, 0.0f, 10.0f, 10.0f);
        quadTree.insert(object);

        // top left
        Rectangle object2 = new Rectangle(0.0f, 30.0f, 10.0f, 10.0f);
        quadTree.insert(object2);

        // top right
        Rectangle object3 = new Rectangle(30.0f, 30.0f, 10.0f, 10.0f);
        quadTree.insert(object3);

        //center object
        Rectangle object4 = new Rectangle(20.0f, 20.0f, 10.0f, 10.0f);
        quadTree.insert(object4);

        Array<Rectangle> returnObjects = new Array<Rectangle>();
        // get collisions with object
        returnObjects = quadTree.retrieve(returnObjects, object);
        // only check itself
        assertEquals(2, returnObjects.size);
        assertEquals(object, returnObjects.get(0));
        assertEquals(object4, returnObjects.get(1));

        returnObjects.clear();
        returnObjects = quadTree.retrieve(returnObjects, object2);
        assertEquals(2, returnObjects.size);
        assertEquals(object2, returnObjects.get(0));
        assertEquals(object4, returnObjects.get(1));

        returnObjects.clear();
        returnObjects = quadTree.retrieve(returnObjects, object3);
        assertEquals(2, returnObjects.size);
        assertEquals(object3, returnObjects.get(0));
        assertEquals(object4, returnObjects.get(1));

        returnObjects.clear();
        returnObjects = quadTree.retrieve(returnObjects, object4);
        assertEquals(4, returnObjects.size);
        assertEquals(object, returnObjects.get(0));
        assertEquals(object2, returnObjects.get(1));
        assertEquals(object3, returnObjects.get(2));
        assertEquals(object4, returnObjects.get(3));

        // quadtree has split
        QuadTree[] internalNodes = quadTree.nodes;
        assertEquals(4, internalNodes.length);

        // quadtree root contains no objects
        assertEquals(0, quadTree.objects.size);

        //quad 0 should contain the central node
        assertEquals(0, internalNodes[0].objects.size);
        assertEquals(1, internalNodes[1].objects.size);
        assertEquals(0, internalNodes[2].objects.size);
        assertEquals(0, internalNodes[3].objects.size);

        assertEquals(object4, internalNodes[1].objects.get(0));

        QuadTree[] internalNodesLevel2 =  internalNodes[1].nodes;
        assertEquals(0, internalNodesLevel2[0].objects.size);
        assertEquals(1, internalNodesLevel2[1].objects.size);
        assertEquals(1, internalNodesLevel2[2].objects.size);
        assertEquals(1, internalNodesLevel2[3].objects.size);

        assertEquals(object, internalNodesLevel2[1].objects.get(0));
        assertEquals(object2, internalNodesLevel2[2].objects.get(0));
        assertEquals(object3, internalNodesLevel2[3].objects.get(0));

    }
}
