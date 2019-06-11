package jobicade.hotswap;

public interface CommonProxy {
    void init();
    void trySuppressInvTweaks();
    void rotate(int rows, boolean wholeRow);

    public static class ProxyStub implements CommonProxy {
        @Override
        public void init() {}

        @Override
        public void trySuppressInvTweaks() {}

        @Override
        public void rotate(int rows, boolean wholeRow) {}
    }
}
