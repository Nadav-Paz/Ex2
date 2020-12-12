package api;

/**
 * 
 */

/**
 * @author Nadav
 *
 */
public class Geo implements geo_location {

	private double x, y, z;
	
	Geo(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	Geo(){
		this(0,0,0);
	}
	
	Geo(geo_location geo){
		this(geo.x(), geo.y(), geo.z());
	}
	
	@Override
	public double x() {
		return this.x;
	}

	@Override
	public double y() {
		return this.y;
	}

	@Override
	public double z() {
		return this.z;
	}

	@Override
	public double distance(geo_location g) {
		double dx = this.x - g.x();
		double dy = this.y - g.y();
		double dz = this.z - g.z();
		double dis = dx * dx + dy * dy + dz * dz;
		return Math.sqrt(dis);
	}
	
	public double distance() {
		return distance(new Geo());
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof geo_location)) {
			return false;
		}
		geo_location geo = (geo_location)o;
		return this.x == geo.x() && this.y == geo.y() && this.z == geo.z();
	}
	public String toString()
	{
		return ("("+this.x+","+this.y+","+this.z+")");
	}
}