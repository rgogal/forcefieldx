//******************************************************************************
//
// Title:       Force Field X.
// Description: Force Field X - Software for Molecular Biophysics.
// Copyright:   Copyright (c) Michael J. Schnieders 2001-2025.
//
// This file is part of Force Field X.
//
// Force Field X is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License version 3 as published by
// the Free Software Foundation.
//
// Force Field X is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// You should have received a copy of the GNU General Public License along with
// Force Field X; if not, write to the Free Software Foundation, Inc., 59 Temple
// Place, Suite 330, Boston, MA 02111-1307 USA
//
// Linking this library statically or dynamically with other modules is making a
// combined work based on this library. Thus, the terms and conditions of the
// GNU General Public License cover the whole combination.
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules, and
// to copy and distribute the resulting executable under terms of your choice,
// provided that you also meet, for each linked independent module, the terms
// and conditions of the license of that module. An independent module is a
// module which is not derived from or based on this library. If you modify this
// library, you may extend this exception to your version of the library, but
// you are not obligated to do so. If you do not wish to do so, delete this
// exception statement from your version.
//
//******************************************************************************
package ffx.algorithms.groovy;

import ffx.algorithms.misc.AlgorithmsTest;
import ffx.numerics.estimator.FreeEnergyDifferenceReporter;
import org.junit.Assert;
import org.junit.Test;

public class BARDualTopologyTest extends AlgorithmsTest {

  /**
   * Tests BAR script with nw input.
   */
  // @Test
  public void testBAR() {

    if (!ffxOpenMM) {
      return;
    }

    String hispath = getResourcePath("testBar/dualTopology/HIS_solv.20.xyz");
    String hidpath = getResourcePath("testBar/dualTopology/HID_solv.20.xyz");

    // Set-up the input arguments for the script.
    String[] args = {"-t", "298", "--nw", "8", "--ac", "28", hispath, hidpath};
    binding.setVariable("args", args);
    // Evaluate the script.
    BAR bar = new BAR(binding).run();
    algorithmsScript = bar;

    System.out.println("after run");

    FreeEnergyDifferenceReporter reporter = bar.getReporter();

    double expectedFepFor = -36.4647;
    double actualFepFor = reporter.getForwardTotalFEDifference();
    Assert.assertEquals(expectedFepFor, actualFepFor, 0.7);

    double expectedFepBack = -36.0005;
    double actualFepBack = reporter.getBackwardTotalFEDifference();
    Assert.assertEquals(expectedFepBack, actualFepBack, 0.7);

    double expectedhFor = -8.7772;
    double actualhFor = reporter.getForwardTotalEnthalpyChange();
    Assert.assertEquals(expectedhFor, actualhFor, 70);

    double expectedhBack = 27.7404;
    double actualhBack = reporter.getBackwardTotalEnthalpyChange();
    Assert.assertEquals(expectedhBack, actualhBack, 60);

    double expectedhBAR = 14.188;
    double actualhBAR = reporter.getBarBSTotalEnthalpyChange();
    Assert.assertEquals(expectedhBAR, actualhBAR, 7);

    double expectedBARIteration = -35.7346;
    double actualBARIteration = reporter.getBarIterTotalFEDiff();
    Assert.assertEquals(expectedBARIteration, actualBARIteration, 1.0);

    double expectedBARBootstrap = -35.9829;
    double actualBARBootstrap = reporter.getBarBSTotalFEDiff();
    Assert.assertEquals(expectedBARBootstrap, actualBARBootstrap, 1.0);
  }

  @Test
  public void testBARHelp() {
    String[] args = {"-h"};
    binding.setVariable("args", args);

    // Evaluate the script.
    BAR bar = new BAR(binding).run();
    algorithmsScript = bar;
  }

}
