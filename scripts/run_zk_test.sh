BUILD_TARGET_PATH=./../target
M2_REPO_PATH=~/.m2/repository
CP=.:$BUILD_TARGET_PATH/test-classes:$BUILD_TARGET_PATH/classes::$M2_REPO_PATH/junit/junit/4.10/junit-4.10.jar:$M2_REPO_PATH/org/hamcrest/hamcrest-core/1.1/hamcrest-core-1.1.jar:$M2_REPO_PATH/org/apache/commons/commons-collections4/4.0/commons-collections4-4.0.jar:$M2_REPO_PATH/org/apache/zookeeper/zookeeper/3.4.6/zookeeper-3.4.6.jar:$M2_REPO_PATH/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar:$M2_REPO_PATH/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar:$M2_REPO_PATH/log4j/log4j/1.2.16/log4j-1.2.16.jar:$M2_REPO_PATH/jline/jline/0.9.94/jline-0.9.94.jar:$M2_REPO_PATH/io/netty/netty/3.7.0.Final/netty-3.7.0.Final.jar

java -Dfile.encoding=UTF-8 -classpath $CP x.commons.lock.distributed.ZooKeeperLockGlobalTest $1 $2 $3
