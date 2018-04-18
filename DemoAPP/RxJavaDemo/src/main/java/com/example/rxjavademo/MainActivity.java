package com.example.rxjavademo;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DDLog.setDebug(true);

        //被观察者绑定观察者
//        observable1.subscribe(observer);
//        observable2.subscribe(observer);
//        observable3.subscribe(observer);
//        observable4.subscribe(observer2);
//        observable5.subscribe(observer1);
//        observable6.subscribe(observer2);
//        observable7.subscribe(observer1);
//        setObserver();
//        test1();
//        test2();
//        test3();
//        test4();
        test5();
    }


    //Observer 观察者 决定事件触发后需要做什么
    Observer<String> observer = new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
            //事件异常时用于解除订阅
            //d.dispose();
        }

        @Override
        public void onNext(String s) {
            Toast.makeText(getApplicationContext(), "" + s, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e) {
            //事件队列异常
        }

        @Override
        public void onComplete() {
            //事件队列没有东西之后调用
        }
    };


    Observer<Integer> observer1 = new Observer<Integer>() {
        @Override
        public void onSubscribe(Disposable d) {
            //事件异常时用于解除订阅
            //d.dispose();
        }

        @Override
        public void onNext(Integer s) {
            Toast.makeText(getApplicationContext(), "" + s, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e) {
            //事件队列异常
        }

        @Override
        public void onComplete() {
            //事件队列没有东西之后调用
        }
    };


    Observer<Long> observer2 = new Observer<Long>() {
        @Override
        public void onSubscribe(Disposable d) {
            //事件异常时用于解除订阅
            //d.dispose();
        }

        @Override
        public void onNext(Long s) {
            Toast.makeText(getApplicationContext(), "计时器", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e) {
            //事件队列异常
        }

        @Override
        public void onComplete() {
            //事件队列没有东西之后调用
        }
    };


    // 被观察者（创建方式1）
    Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> e) throws Exception {
            //执行一些其他操作
            // .............
            // 执行完毕，触发回调，通知观察者
            e.onNext("我来发射数据");
        }
    });

    //被观察者（创建方式2）
    Observable<String> observable2 = Observable.just("hello");


    List<String> list = new ArrayList<String>();

    {
        for (int i = 0; i < 10; i++) {
            list.add("Hello" + i);
        }
    }

    //被观察者（创建方式3）fromIterable会遍历list并且每次item都调用一次onNext（）；
    Observable<String> observable3 = Observable.fromIterable((Iterable<String>) list);


    //被观察者（创建方式4）interval每两秒发送一次 相当于timmer；
    Observable<Long> observable4 = Observable.interval(2, TimeUnit.SECONDS);

    //被观察者（创建方式5）range连续发送1--20的数字给onnext（）；
    Observable<Integer> observable5 = Observable.range(1, 20);


    //被观察者（创建方式6）time延迟两秒发送特殊值给onnext（）；
    Observable<Long> observable6 = Observable.timer(2, TimeUnit.SECONDS);

    //被观察者（创建方式7）repeat可以重复调用observable7.repeat()；
    Observable<Integer> observable7 = Observable.just(123).repeat();

    //Consumer观察者 accept为接受数据接口
    private void setObserver() {
        Observable.just("hello is me!!").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //睡眠10秒后，才进行订阅 仍然从0开始，表示Observable内部逻辑刚开始执行
    //说明被观察者只有绑定观察者后才开始运行代码
    private void test1() {
        Observable<Long> observable = Observable.interval(2, TimeUnit.SECONDS);
        Observer<Long> observer = new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Long aLong) {
                DDLog.e("" + aLong);
                Toast.makeText(getApplicationContext(), "" + aLong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        SystemClock.sleep(10000);
        observable.subscribe(observer);
    }

    //map()操作符，就是把原来的Observable对象转换成另一个Observable对象，同时将传输的数据进行一些灵活的操作，方便Observer获得想要的数据形式。
    private void test2() {
        //function 第一个参数为传入参数 第二个参数为传入onNext（）里的值
        Observable<Integer> observable = Observable.just("hello").map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return s.length();
            }
        });
        observable.subscribe(observer1);
    }


    //flatMap()对于数据的转换比map()更加彻底，如果发送的数据是集合，flatmap()重新生成一个Observable对象，
    // 并把数据转换成Observer想要的数据形式。它可以返回任何它想返回的Observable对象。
    private void test3() {
        Observable<Object> observable = Observable.just(list).flatMap(new Function<List<String>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(List<String> strings) throws Exception {
                return Observable.fromIterable(strings);
            }
        });
        observable.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //take()操作符：输出最多指定数量的结果。
    private void test4() {
        Observable.just(list).flatMap(new Function<List<String>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(List<String> strings) throws Exception {
                return Observable.fromIterable(strings);
            }
        }).take(5).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object s) throws Exception {
                Toast.makeText(getApplicationContext(), s.toString(), Toast.LENGTH_SHORT).show();//最多输出5
            }
        });
    }

    //subscribeOn(): 指定Observable(被观察者)所在的线程
    //observeOn(): 指定 Observer(观察者)所运行在的线程
    /*
    Scheduler 的 API

    ● Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。

    ●Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。

    ●Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，
    区别在于 io() 的内部实现是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。
    不要把计算工作放在 io() 中，可以避免创建不必要的线程。

    ●Schedulers.computation(): **计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作
    ，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O
    操作的等待时间会浪费 CPU。

    ● Android 还有一个专用的** AndroidSchedulers.mainThread()**，它指定的操作将在 Android 主线程运行。

     */
    private void test5() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                DDLog.d("所在的线程：", Thread.currentThread().getName());
                DDLog.d("发送的数据:", 1 + "");
                e.onNext(1);
            }
        })
        .subscribeOn(Schedulers.io())//被观察者所在线程
        .observeOn(AndroidSchedulers.mainThread())//观察者所在线程
        .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer i) throws Exception {
                        DDLog.d("所在的线程：", Thread.currentThread().getName());
                        DDLog.d("接收到的数据:", "integer:" + i);
                    }
                });
    }

    private void test6(){

    }


}
