import javafx.scene.*;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Three_D{

    private static final double SCALE = 80.0;
    private static final double SPHERE_RADIUS = 3.0;

    public void show(WordStorage holder, int xIndex, int yIndex, int zIndex) {
        if (holder == null) {
            throw new IllegalArgumentException("WordHolder cannot be null");
        }

        Group pointsGroup = new Group();

        for (Word word : holder.getWords()) {
            Vector pca = word.getPcaVector();

            if (pca == null) {
                continue;
            }

            if (xIndex >= pca.size() || yIndex >= pca.size() || zIndex >= pca.size()) {
                throw new IllegalArgumentException("PCA axis index out of bounds");
            }

            double x = pca.getValue(xIndex) * SCALE;
            double y = pca.getValue(yIndex) * SCALE;
            double z = pca.getValue(zIndex) * SCALE;

            Sphere sphere = new Sphere(SPHERE_RADIUS);
            sphere.setTranslateX(x);
            sphere.setTranslateY(y);
            sphere.setTranslateZ(z);

            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(Color.DODGERBLUE);
            sphere.setMaterial(material);

            Tooltip.install(sphere, new Tooltip(word.getWord()));

            pointsGroup.getChildren().add(sphere);
        }

        Group root3D = new Group(pointsGroup);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-800);
        camera.setNearClip(0.1);
        camera.setFarClip(5000);

        SubScene subScene = new SubScene(root3D, 1000, 700, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);

        addMouseRotation(subScene, pointsGroup);

        Group mainRoot = new Group(subScene);

        Scene scene = new Scene(mainRoot, 1000, 700, true);

        Stage stage = new Stage();
        stage.setTitle("LatentSpace Explorer - 3D View");
        stage.setScene(scene);
        stage.show();
    }

    private void addMouseRotation(SubScene scene, Group group) {
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

        group.getTransforms().addAll(rotateX, rotateY);

        final double[] mouseOldX = new double[1];
        final double[] mouseOldY = new double[1];

        scene.setOnMousePressed(event -> {
            mouseOldX[0] = event.getSceneX();
            mouseOldY[0] = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mouseOldX[0];
            double deltaY = event.getSceneY() - mouseOldY[0];

            rotateY.setAngle(rotateY.getAngle() + deltaX * 0.3);
            rotateX.setAngle(rotateX.getAngle() - deltaY * 0.3);

            mouseOldX[0] = event.getSceneX();
            mouseOldY[0] = event.getSceneY();
        });
    }
}