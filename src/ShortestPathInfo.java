class ShortestPathInfo<T, U, V> {
    final T list;
    final U bool;
    final V dist;

    ShortestPathInfo(T list, U bool, V dist) {
        this.list= list;
        this.bool= bool;
        this.dist = dist;
    }
}