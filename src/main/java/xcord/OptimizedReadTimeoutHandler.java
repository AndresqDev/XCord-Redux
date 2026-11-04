package xcord;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.netty.PipelineUtils;

public class OptimizedReadTimeoutHandler
extends ChannelDuplexHandler {
    private final long timeoutMillis;
    private Future<?> timeoutFuture;
    private long lastReadMillis;
    private byte state;
    private boolean reading;
    private boolean timedOut = false;

    public OptimizedReadTimeoutHandler(long l, TimeUnit timeUnit) {
        ObjectUtil.checkNotNull(timeUnit, "unit");
        this.timeoutMillis = Math.max(timeUnit.toMillis(l), 1L);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {
        if (channelHandlerContext.channel().isActive() && channelHandlerContext.channel().isRegistered()) {
            this.a(channelHandlerContext);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {
        this.a();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext channelHandlerContext) throws Exception {
        if (channelHandlerContext.channel().isActive()) {
            this.a(channelHandlerContext);
        }
        super.channelRegistered(channelHandlerContext);
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        this.a(channelHandlerContext);
        super.channelActive(channelHandlerContext);
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        this.a();
        super.channelInactive(channelHandlerContext);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        this.reading = true;
        channelHandlerContext.fireChannelRead(object);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
        if (this.reading) {
            this.lastReadMillis = System.currentTimeMillis();
            this.reading = false;
        }
        channelHandlerContext.fireChannelReadComplete();
    }

    private void a(ChannelHandlerContext channelHandlerContext) {
        switch (this.state) {
            case 1: 
            case 2: {
                return;
            }
        }
        this.state = 1;
        this.lastReadMillis = System.currentTimeMillis();
        this.timeoutFuture = channelHandlerContext.executor().schedule(new TimeoutTask(channelHandlerContext), this.timeoutMillis, TimeUnit.MILLISECONDS);
    }

    private Future<?> a(ChannelHandlerContext channelHandlerContext, Runnable runnable, long l, TimeUnit timeUnit) {
        return channelHandlerContext.executor().schedule(runnable, l, timeUnit);
    }

    private void a() {
        this.state = (byte)2;
        if (this.timeoutFuture != null) {
            this.timeoutFuture.cancel(false);
            this.timeoutFuture = null;
        }
    }

    private final class TimeoutTask
    implements Runnable {
        private final ChannelHandlerContext context;

        TimeoutTask(ChannelHandlerContext channelHandlerContext) {
            this.context = channelHandlerContext;
        }

        @Override
        public void run() {
            if (!OptimizedReadTimeoutHandler.this.timedOut && this.context.channel().isOpen()) {
                long l = OptimizedReadTimeoutHandler.this.timeoutMillis;
                if (!OptimizedReadTimeoutHandler.this.reading) {
                    l -= System.currentTimeMillis() - OptimizedReadTimeoutHandler.this.lastReadMillis;
                }
                if (l <= 0L) {
                    OptimizedReadTimeoutHandler.this.timedOut = true;
                    try {
                        this.context.fireExceptionCaught(ReadTimeoutException.INSTANCE);
                        if (this.context.channel().isActive()) {
                            PipelineUtils.CHANNEL_UTILITY.close(this.context.channel());
                        }
                    }
                    catch (Throwable throwable) {
                        this.context.fireExceptionCaught(throwable);
                    }
                } else {
                    OptimizedReadTimeoutHandler.this.timeoutFuture = OptimizedReadTimeoutHandler.this.a(this.context, this, l, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}

