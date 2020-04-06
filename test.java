package Redis�ֲ�ʽ��;
import redis.clients.jedis.Jedis;
public class test {
	//����key
	private static final String key = "DistributeRedisLock";
	//����
	private static Integer count = 0;
	
	public static void main(String[] args) {
		for(int i=0;i<10;i++) {
			new Thread(new Runnable(){
				@Override
				public void run() {
					//��ȡRedis����
					Jedis jedis = new Jedis("localhost");
					jedis.auth("123456");	
					try {
						while(true) {
							//��ȡ��
							if(jedis.setnx(key,Thread.currentThread().getName()) == 1) {
								//��ȡ��ʼʱ�������
								long start = System.currentTimeMillis(); 
								try {
									System.out.println("�߳�("+Thread.currentThread().getName()+")��ȡ��������ʼ���в���");
									//����������ʱ��
									jedis.expire(key, 10); 
									count++;
									System.out.println(count);
									break;
								}finally {
									
									//���֮ǰ���õ�����δ���ڣ����ͷŵ�
									if(System.currentTimeMillis() < start+10*10){
										jedis.del(key);
										System.out.println("����ִ����ɣ��ͷ���");
									}
									
								}
							}else {
								//��ȡ��ʱ���ز���1����˵��ĳ���̻߳�ȡ������
								try {
									//�ȴ�100ms
									Thread.sleep(100);
								}catch(InterruptedException e) {
									e.printStackTrace();
								}
							}//if else
						}//while
					}catch(Exception e) {
						e.printStackTrace();
					}finally{
						//�ͷ�Redis����
						jedis.disconnect();
					}
				}
			}).start(); //run
		}
	}

}
