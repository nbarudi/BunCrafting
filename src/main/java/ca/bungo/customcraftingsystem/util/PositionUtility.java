package ca.bungo.customcraftingsystem.util;

import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PositionUtility {


    public static double getAngleToLocation(Vector vecToLoc){
        return Math.toDegrees(Math.atan2(vecToLoc.getZ(), vecToLoc.getX()));
    }

    public static List<Location> teleportInCircle(Location location, double radius, int numPoints) {
        double angle = 0;
        double offset = 360.0 / numPoints;
        final double centerX = location.getX();
        final double centerY = location.getY();
        final double centerZ = location.getZ();

        List<Location> locations = new ArrayList<>();

        while(!(angle > 360 - offset)) {
            double radians = Math.toRadians(angle);
            double x = centerX + radius * Math.cos(radians);
            double z = centerZ + radius * Math.sin(radians);
            Location loc = new Location(location.getWorld(), x, centerY, z);
            Vector toVec = loc.toVector().subtract(location.toVector());
            loc.setYaw((float) getAngleToLocation(toVec));
            locations.add(loc);
            angle += offset;
        }
        return locations;
    }

    public static List<Location> teleportInCircle(Location location, double radius, int numPoints, float posoffset) {
        double angle = 0;
        double offset = 360.0 / numPoints;
        final double centerX = location.getX();
        final double centerY = location.getY();
        final double centerZ = location.getZ();

        List<Location> locations = new ArrayList<>();

        while(!(angle > 360 - offset)) {
            double radians = Math.toRadians(angle + posoffset);
            double x = centerX + radius * Math.cos(radians);
            double y = centerY + radius * (Math.sin(radians) * Math.cos(radians)); //+ (Math.sin(3*radians) * Math.cos(3*radians))); //2 + cos(10x) + 2sin(5x) //
            double z = centerZ + radius * Math.sin(radians);
            Location loc = new Location(location.getWorld(), x, y, z);
            Vector toVec = loc.toVector().subtract(location.toVector());
            loc.setYaw((float) getAngleToLocation(toVec));
            locations.add(loc);
            angle += offset;
        }
        return locations;
    }

}
