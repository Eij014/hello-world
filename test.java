package Redis分布式锁;
import redis.clients.jedis.Jedis;
public class test {
	//锁的key
	private static final String key = "DistributeRedisLock";
	//计数
	private static Integer count = 0;
	
	public static void main(String[] args) {
		for(int i=0;i<10;i++) {
			new Thread(new Runnable(){
				@Override
				public void run() {
					//获取Redis连接
					Jedis jedis = new Jedis("localhost");
					jedis.auth("123456");	
					try {
						while(true) {
							//获取锁
							if(jedis.setnx(key,Thread.currentThread().getName()) == 1) {
								//获取起始时间毫秒数
								long start = System.currentTimeMillis(); 
								try {
									System.out.println("线程("+Thread.currentThread().getName()+")获取到锁，开始进行操作");
									//设置锁过期时间
									jedis.expire(key, 10); 
									count++;
									System.out.println(count);
									break;
								}finally {
									
									//如果之前设置的锁还未过期，则释放掉
									if(System.currentTimeMillis() < start+10*10){
										jedis.del(key);
										System.out.println("操作执行完成，释放锁");
									}
									
								}
							}else {
								//获取锁时返回不是1，则说明某个线程获取到了锁
								try {
									//等待100ms
									Thread.sleep(100);
								}catch(InterruptedException e) {
									e.printStackTrace();
								}
							}//if else
						}//while
					}catch(Exception e) {
						e.printStackTrace();
					}finally{
						//释放Redis连接
						jedis.disconnect();
					}
				}
			}).start(); //run
		}
	}

}
