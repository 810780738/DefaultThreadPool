package com.xiaozhu.ThreadPoolHttpServer.thread;

/**
 * 线程池接口
 */
public interface ThreadPool<Jop extends Runnable> {
    //执行一个jop，需要实现Runnable接口
    void execute(Jop jop);
    //关闭线程池
    void shutdown();
    //增加一个线程工作者
    void addWorkers(int num);
    //删除一个线程工作者
    void removeWorker(int num);
    //获取正在工作的线程工作者
    int getJopSize();
}
