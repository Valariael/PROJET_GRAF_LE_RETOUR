/**
 * This class allows us to make pairs of values/data.
 *
 * @param <T> 'list'
 * @param <U> 'dist'
 */
class LongestPathInfo<T, U> {
    final T list;
    final U dist;

    /**
     * The default constructor for the pair of values/data.
     *
     * @param list first data
     * @param dist second data
     */
    LongestPathInfo(T list, U dist) {
        this.list = list;
        this.dist = dist;
    }
}