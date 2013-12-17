package edu.stanford.isis.epadws.processing.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;

import edu.stanford.isis.epad.common.util.EPADLogger;
import edu.stanford.isis.epadws.processing.model.DatabaseState;

/**
 * @author amsnyder
 */
public class MySqlInstance
{
	private static EPADLogger logger = EPADLogger.getInstance();

	private static final String USER = "pacs";
	private static final String PWD = "pacs";

	private static final MySqlInstance ourInstance = new MySqlInstance();

	private MySqlConnectionPool connectionPool;
	private MySqlQueries mySqlQueries;

	private final AtomicReference<DatabaseState> dbState = new AtomicReference<DatabaseState>(DatabaseState.INIT);

	private long startupTime = -1;

	public static MySqlInstance getInstance()
	{
		return ourInstance;
	}

	private MySqlInstance()
	{
		initConnectionPool();
	}

	private void initConnectionPool()
	{
		try {
			logger.info("Creating connection pool.");
			createConnectionPool();
			mySqlQueries = new MySqlQueriesImpl(connectionPool);
		} catch (Exception e) {
			logger.severe("Failed to create connection pool", e);
			dbState.set(DatabaseState.ERROR);
		}
	}

	public void startup()
	{
		try {
			dbState.set(DatabaseState.STARTING);
			long time = System.currentTimeMillis();

			if (!tablesUpToDate()) {
				logger.info("IMPORTANT: NEED to add the epaddb tables using MySQL command-line!!");
				// updateMySqlTables();
			} else {
				logger.info("ePad's extra MySQL tables appear to be up to date.");
			}
			startupTime = System.currentTimeMillis() - time;
			dbState.set(DatabaseState.READY);
			logger.info("Database took " + startupTime + " ms to start.");
		} catch (Exception e) {
			logger.severe("Failed to start-up database", e);
			dbState.set(DatabaseState.ERROR);
		}
	}

	public long getStartupTime()
	{
		return startupTime;
	}

	private boolean tablesUpToDate()
	{
		boolean result = false;
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("Checking MySQL database.");
			conn = connectionPool.getConnection();
			s = conn.createStatement();

			try {
				rs = s.executeQuery("SELECT version FROM epaddb.dbversion");
			} catch (Exception e) {
				logger.info("SELECT version FROM epaddb.dbversion had error: " + e.getMessage());
				return false;
			}
			if (rs.next()) {
				result = true;
				sb.append("DB Version is: ").append(rs.getString("version")).append(" ");
			}
		} catch (SQLException sqle) {
			logger.warning("SQL Error when checking for database version", sqle);
			result = false;
		} finally {
			logger.info(sb.toString());
			DatabaseUtils.close(rs);
			DatabaseUtils.close(s);
			DatabaseUtils.close(conn);
		}
		return result;
	}

	public void shutdown()
	{
		dbState.set(DatabaseState.SHUTDOWN);
		long time = System.currentTimeMillis();
		logger.info("Shutting down database.");

		closeConnectionPool();
		logger.info("The database took " + (System.currentTimeMillis() - time) + " ms, to shutdown.");
	}

	public MySqlQueries getMysqlQueries()
	{
		return mySqlQueries;
	}

	private void createConnectionPool() throws SQLException
	{
		String localHostConnStr = "jdbc:mysql://localhost:3306?autoReconnect=true";

		logger.info("MySql using connection string: " + localHostConnStr);

		connectionPool = new MySqlConnectionPool(localHostConnStr, USER, PWD);
	}

	private void closeConnectionPool()
	{
		connectionPool.dispose();
	}

	public int getConnectionPoolAvailCount()
	{
		return connectionPool.availableConnectionCount();
	}

	public int getConnectionPoolUsedCount()
	{
		return connectionPool.usedConnectionCount();
	}
}
