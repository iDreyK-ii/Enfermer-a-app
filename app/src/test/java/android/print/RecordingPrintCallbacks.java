package android.print;

/** Test-only adapters: Android's callback constructors are package-private. Never packaged in the app. */
public final class RecordingPrintCallbacks {
    private RecordingPrintCallbacks() {}

    public static final class Layout extends PrintDocumentAdapter.LayoutResultCallback {
        public PrintDocumentInfo info;
        public boolean cancelled;
        public CharSequence error;
        @Override public void onLayoutFinished(PrintDocumentInfo info, boolean changed) { this.info = info; }
        @Override public void onLayoutCancelled() { cancelled = true; }
        @Override public void onLayoutFailed(CharSequence error) { this.error = error; }
    }

    public static final class Write extends PrintDocumentAdapter.WriteResultCallback {
        public PageRange[] pages;
        public boolean cancelled;
        public CharSequence error;
        @Override public void onWriteFinished(PageRange[] pages) { this.pages = pages; }
        @Override public void onWriteCancelled() { cancelled = true; }
        @Override public void onWriteFailed(CharSequence error) { this.error = error; }
    }
}
