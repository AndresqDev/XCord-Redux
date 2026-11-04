package xcord.network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.netty.PipelineUtils;

public class OptimizedReadTimeoutHandler extends ChannelDuplexHandler {
    private final long timeoutMillis;
    private Future<?> timeoutFuture;
    private long lastReadMillis;
    private byte state;
    private boolean reading;
    private boolean timedOut = false;

    public OptimizedReadTimeoutHandler(long timeout, TimeUnit unit) {
        ObjectUtil.checkNotNull(unit, "unit");
        this.timeoutMillis = Math.max(unit.toMillis(timeout), 1L);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) throws Exception {
        if (context.channel().isActive() && context.channel().isRegistered()) {
            this.startTimeout(context);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) throws Exception {
        this.stopTimeout();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext context) throws Exception {
        if (context.channel().isActive()) {
            this.startTimeout(context);
        }
        super.channelRegistered(context);
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        this.startTimeout(context);
        super.channelActive(context);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        this.stopTimeout();
        super.channelInactive(context);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        this.reading = true;
        context.fireChannelRead(message);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        if (this.reading) {
            this.lastReadMillis = System.currentTimeMillis();
            this.reading = false;
        }
        context.fireChannelReadComplete();
    }

    private void startTimeout(ChannelHandlerContext context) {
        if (this.state == 1 || this.state == 2) {
            return;
        }
        this.state = 1;
        this.lastReadMillis = System.currentTimeMillis();
        this.timeoutFuture = context.executor().schedule(new TimeoutTask(context), this.timeoutMillis, TimeUnit.MILLISECONDS);
    }

    private Future<?> scheduleTimeout(ChannelHandlerContext context, Runnable task, long delay, TimeUnit unit) {
        return context.executor().schedule(task, delay, unit);
    }

    private void stopTimeout() {
        this.state = 2;
        if (this.timeoutFuture != null) {
            this.timeoutFuture.cancel(false);
            this.timeoutFuture = null;
        }
    }

    private final class TimeoutTask implements Runnable {
        private final ChannelHandlerContext context;

        TimeoutTask(ChannelHandlerContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            if (OptimizedReadTimeoutHandler.this.timedOut || !this.context.channel().isOpen()) {
                return;
            }
            long remainingMillis = OptimizedReadTimeoutHandler.this.timeoutMillis;
            if (!OptimizedReadTimeoutHandler.this.reading) {
                remainingMillis -= System.currentTimeMillis() - OptimizedReadTimeoutHandler.this.lastReadMillis;
            }
            if (remainingMillis <= 0L) {
                OptimizedReadTimeoutHandler.this.timedOut = true;
                try {
                    this.context.fireExceptionCaught(ReadTimeoutException.INSTANCE);
                    if (this.context.channel().isActive()) {
                        PipelineUtils.CHANNEL_UTILITY.close(this.context.channel());
                    }
                } catch (Throwable cause) {
                    this.context.fireExceptionCaught(cause);
                }
            } else {
                OptimizedReadTimeoutHandler.this.timeoutFuture = OptimizedReadTimeoutHandler.this.scheduleTimeout(this.context, this, remainingMillis, TimeUnit.MILLISECONDS);
            }
        }
    }
}
