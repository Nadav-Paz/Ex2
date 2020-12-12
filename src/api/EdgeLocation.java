package api;

public class EdgeLocation implements edge_location {

    private directed_weighted_graph graph;
    private geo_location geo;
    private edge_data edge;
    private double value = 0;

    public EdgeLocation(directed_weighted_graph graph, edge_data edge, geo_location geo, double value) {
        this.graph = graph;
        this.edge = edge;
        this.geo = geo;
        this.value = value;
    }

    public EdgeLocation(directed_weighted_graph graph, geo_location geo, double value) {
        this(graph, null, geo, value);
    }

    public edge_data getEdge() {
        if (this.edge != null) return this.edge;
        else {
            System.err.println("Null edge");
            return null;
        }
    }

    public double getValue() {
        return this.value;
    }

    public geo_location geoLocation() {
        if (this.geo != null) return this.geo;
        else {
            System.err.println("Null location");
            return null;
        }
    }

    public boolean setLocation(geo_location new_geo) {
        geo_location old_geo = this.geo;
        this.geo = new_geo;

        if (getRatio() != -1)
            return true;

        this.geo = old_geo;
        return false;
    }

    public boolean setEdge(edge_data new_edge) {
        edge_data old_edge = this.edge;
        this.edge = new_edge;

        if (getRatio() != -1) {
            return true;
        }
        this.edge = old_edge;
        return false;
    }

    @Override
    public double getRatio() {
        if (this.edge == null) {
            System.err.println("null data");
            return -1;
        }

        node_data src_node = this.graph.getNode(this.edge.getSrc());
        node_data dst_node = this.graph.getNode(this.edge.getDest());

        if (src_node != null && dst_node != null && this.geo != null && !src_node.equals(dst_node)) {
            geo_location src = src_node.getLocation();
            geo_location dst = dst_node.getLocation();

            if (this.geo.equals(src))
                return 0;
            if (this.geo.equals(dst))
                return 1;

            double dist_src_geo = src.distance(this.geo);
            double dist_src_dst = src.distance(dst);
            final double close_enough = 0.000000001;

            if (this.geo.x() - src.x() != dst.x() - src.x()) {
                if (Math.abs(((this.geo.x() - src.x()) / dist_src_geo) - ((dst.x() - src.x()) / dist_src_dst)) < close_enough) {
//                System.err.println("not inline err");
                    return -1;
                }
            }

            if (this.geo.y() - src.y() != dst.y() - src.y()) {
                if (Math.abs(((this.geo.y() - src.y()) / dist_src_geo) - ((dst.y() - src.y()) / dist_src_dst)) < close_enough) {
//                System.err.println("not inline err");
                    return -1;
                }
            }
            if (this.geo.z() - src.z() != dst.z() - src.z()) {
                if (Math.abs(((this.geo.z() - src.z()) / dist_src_geo) - ((dst.z() - src.z()) / dist_src_dst)) < close_enough) {
//                System.err.println("not inline err");
                    return -1;
                }
            }

            return dist_src_geo / dist_src_dst;

        } else {
            System.err.println("null data");
            return -1;
        }
    }

    @Override
    public String toString() {
        return String.format("location: %s, Ratio: %s", this.geo, getRatio());
    }
}
