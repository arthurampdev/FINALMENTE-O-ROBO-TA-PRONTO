package Leclerc.com;
import robocode.*;
import robocode.util.Utils;
import java.awt.geom.Point2D;

public class Driven_Like_Leclerc extends AdvancedRobot
{

public void run() {

        // Independentes
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        // Roda Roda Radar
        while (true) {
            turnRadarRight(360);
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // Defense
        double changeDirection = 120;
        setTurnRight(e.getBearing() + 150 - (15 * (Math.random() > 0.5 ? 1 : -1)));
		setTurnLeft(50);
        setAhead(changeDirection);

        // Mira 
        double power = 2.5; // Força 
        double bulletSpeed = 20 - 3 * power; // Velocidade 

        // Ângulo absoluto 
        double absBearing = getHeadingRadians() + e.getBearingRadians();

        // Posição inimigo
        double enemyX = getX() + e.getDistance() * Math.sin(absBearing);
        double enemyY = getY() + e.getDistance() * Math.cos(absBearing);

        // Velocidade e direção do inimigo
        double enemyVelocity = e.getVelocity();
        double enemyHeading = e.getHeadingRadians();

        double predictX = enemyX;
        double predictY = enemyY;

        // Até atingir o alvo
        for (int i = 0; i < 30; i++) {
            predictX += Math.sin(enemyHeading) * enemyVelocity;
            predictY += Math.cos(enemyHeading) * enemyVelocity;

            // Previsão do Mapa
            predictX = Math.max(18, Math.min(getBattleFieldWidth() - 18, predictX));
            predictY = Math.max(18, Math.min(getBattleFieldHeight() - 18, predictY));

            double distance = Point2D.distance(getX(), getY(), predictX, predictY);
            if (distance / bulletSpeed <= i) {
                break;
            }
        }

        // Ângulo posição adquirida
        double theta = Utils.normalAbsoluteAngle(Math.atan2(predictX - getX(), predictY - getY()));

        // Canhão até a posição correta
        setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));

        // Se o canhão estiver ok atira
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
            setFire(power);
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // Manobra evasiva 
        back(250);
    }

    public void onHitWall(HitByBulletEvent e) {
        turnRight(90);
        ahead(100);
    }
}