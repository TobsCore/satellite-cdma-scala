package hska.embedded

class ProductionReaper extends Reaper{

  // Shutdown
  def allSoulsReaped(): Unit = context.system.shutdown()

}
