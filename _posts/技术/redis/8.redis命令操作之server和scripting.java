/********* redis serverÃüÁî²Ù×÷ *************/

BGREWRITEAOF -
summary: Asynchronously rewrite the append-only file
since: 1.0.0

BGSAVE -
summary: Asynchronously save the dataset to disk
since: 1.0.0

CLIENT GETNAME -
summary: Get the current connection name
since: 2.6.9

CLIENT KILL ip:port
summary: Kill the connection of a client
since: 2.4.0

CLIENT LIST -
summary: Get the list of client connections
since: 2.4.0

CLIENT SETNAME connection-name
summary: Set the current connection name
since: 2.6.9

CONFIG GET parameter
summary: Get the value of a configuration parameter
since: 2.0.0

CONFIG RESETSTAT -
summary: Reset the stats returned by INFO
since: 2.0.0

CONFIG SET parameter value
summary: Set a configuration parameter to the given value
since: 2.0.0

DBSIZE -
summary: Return the number of keys in the selected database
since: 1.0.0

DEBUG OBJECT key
summary: Get debugging information about a key
since: 1.0.0

DEBUG SEGFAULT -
summary: Make the server crash
since: 1.0.0

FLUSHALL -
summary: Remove all keys from all databases
since: 1.0.0

FLUSHDB -
summary: Remove all keys from the current database
since: 1.0.0

INFO [section]
summary: Get information and statistics about the server
since: 1.0.0

LASTSAVE -
summary: Get the UNIX time stamp of the last successful save to disk
since: 1.0.0

MONITOR -
summary: Listen for all requests received by the server in real time
since: 1.0.0

SAVE -
summary: Synchronously save the dataset to disk
since: 1.0.0

SHUTDOWN [NOSAVE] [SAVE]
summary: Synchronously save the dataset to disk and then shut down the server
since: 1.0.0

SLAVEOF host port
summary: Make the server a slave of another instance, or promote it as master
since: 1.0.0

SLOWLOG subcommand [argument]
summary: Manages the Redis slow queries log
since: 2.2.12

SYNC -
summary: Internal command used for replication
since: 1.0.0

TIME -
summary: Return the current server time
since: 2.6.0

/**************** redis scripting ÃüÁî²Ù×÷ *************/
EVAL script numkeys key [key ...] arg [arg ...]
summary: Execute a Lua script server side
since: 2.6.0

EVALSHA sha1 numkeys key [key ...] arg [arg ...]
summary: Execute a Lua script server side
since: 2.6.0

SCRIPT EXISTS script [script ...]
summary: Check existence of scripts in the script cache.
since: 2.6.0

SCRIPT FLUSH -
summary: Remove all the scripts from the script cache.
since: 2.6.0

SCRIPT KILL -
summary: Kill the script currently in execution.
since: 2.6.0

SCRIPT LOAD script
summary: Load the specified Lua script into the script cache.
since: 2.6.0


