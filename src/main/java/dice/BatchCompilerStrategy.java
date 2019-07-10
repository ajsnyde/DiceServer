package dice;

import java.util.List;

public interface BatchCompilerStrategy {
	public DieBatch compile();

	DieBatch compile(List<DieJob> jobs) throws FixtureCompilationException;
}
