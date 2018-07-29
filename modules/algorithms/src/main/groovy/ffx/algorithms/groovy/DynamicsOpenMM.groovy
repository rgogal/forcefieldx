package ffx.algorithms.groovy

import org.apache.commons.io.FilenameUtils

import ffx.algorithms.MolecularDynamics
import ffx.algorithms.MolecularDynamicsOpenMM
import ffx.algorithms.cli.AlgorithmsScript
import ffx.algorithms.cli.DynamicsOptions
import ffx.algorithms.cli.WriteoutOptions
import ffx.potential.ForceFieldEnergy
import ffx.potential.ForceFieldEnergyOpenMM
import ffx.potential.MolecularAssembly
import ffx.potential.parameters.ForceField

import picocli.CommandLine.Command
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

@Command(description = " Run dynamics on a system using OpenMM.", name = "ffxc DynamicsOpenMM")
class DynamicsOpenMM extends AlgorithmsScript {


    @Mixin
    DynamicsOptions dynamics;

    @Mixin
    WriteoutOptions writeout;

    /**
     * -z or --trajSteps sets the length of the MD trajectory run on the GPU in femtoseconds(defaul is 100 femtoseconds)
     */
    @Option(names = ['-z', '--trajSteps'], paramLabel = '100',
            description = 'Number of steps for each MD Trajectory in femtoseconds')
    int trajSteps = 100
    /**
     * --cf or --coeffOfFriction specifies what the coefficient of friction is to be used with Langevin and Brownian integrators
     */
    @Option(names = ['--cf', '--coeffOfFriction'], paramLabel = '0.01',
            description = 'Coefficient of friction to be used with the Langevin and Brownian integrators')
    double coeffOfFriction = 0.01
    /**
     * -q or --collisionFreq specifies the frequency for particle collision to be used with the Anderson thermostat
     */
    @Option(names = ['-q', '--collisionFreq'], paramLabel = '91.0',
            description = 'Collision frequency to be set when Anderson Thermostat is created: Can be used with Verlet integrator')
    double collisionFreq = 91.0

    /**
     * One or more filenames.
     */
    @Parameters(arity = "1..*", paramLabel = "files",
            description = "XYZ or PDB input files.")
    private List<String> filenames

    private MolecularDynamicsOpenMM molDynOpenMM;

    MolecularDynamicsOpenMM getMolecularDynamics() {
        return molDynOpenMM;
    }

    @Override
    DynamicsOpenMM run() {

        if (!init()) {
            return this
        }

        dynamics.init()

        String modelfilename
        if (filenames != null && filenames.size() > 0) {
            MolecularAssembly[] assemblies = algorithmFunctions.open(filenames.get(0))
            activeAssembly = assemblies[0]
            modelfilename = filenames.get(0)
        } else if (activeAssembly == null) {
            logger.info(helpString())
            return this
        } else {
            modelfilename = activeAssembly.getFile().getAbsolutePath()
        }

        ForceFieldEnergy forceFieldEnergy = activeAssembly.getPotentialEnergy();
        switch (forceFieldEnergy.getPlatform()) {
            case ForceFieldEnergy.Platform.OMM:
            case ForceFieldEnergy.Platform.OMM_CUDA:
            case ForceFieldEnergy.Platform.OMM_OPENCL:
            case ForceFieldEnergy.Platform.OMM_OPTCPU:
            case ForceFieldEnergy.Platform.OMM_REF:
                logger.fine(" Platform is appropriate for OpenMM Dynamics.")
                break
            case ForceFieldEnergy.Platform.FFX:
            default:
                logger.severe(String.format(" Platform %s is inappropriate for OpenMM dynamics. Please explicitly specify an OpenMM platform.",
                        forceFieldEnergy.getPlatform()))
                break
        }


        logger.info(" Starting energy (before .dyn restart loaded):")
        boolean updatesDisabled = activeAssembly.getForceField().getBoolean(ForceField.ForceFieldBoolean.DISABLE_NEIGHBOR_UPDATES, false)
        if (updatesDisabled) {
            logger.info(" This ensures neighbor list is properly constructed from the source file, before coordinates updated by .dyn restart")
        }
        double[] x = new double[forceFieldEnergy.getNumberOfVariables()]
        forceFieldEnergy.getCoordinates(x)
        forceFieldEnergy.energy(x, true)

        logger.info("\n Running molecular dynamics on " + modelfilename)

        // Restart File
        File dyn = new File(FilenameUtils.removeExtension(modelfilename) + ".dyn")
        if (!dyn.exists()) {
            dyn = null
        }

        MolecularDynamics moldyn = MolecularDynamics.dynamicsFactory(activeAssembly, forceFieldEnergy, activeAssembly.getProperties(),
                algorithmListener, dynamics.thermostat, dynamics.integrator)

        if (moldyn instanceof MolecularDynamicsOpenMM) {
            molDynOpenMM = (MolecularDynamicsOpenMM) moldyn
            ForceFieldEnergyOpenMM forceFieldEnergyOpenMM = molDynOpenMM.getForceFieldEnergyOpenMM()
            forceFieldEnergyOpenMM.setCoeffOfFriction(coeffOfFriction)
            forceFieldEnergyOpenMM.setCollisionFreq(collisionFreq)
            molDynOpenMM.setRestartFrequency(dynamics.write)
            molDynOpenMM.setFileType(writeout.getFileType())
            molDynOpenMM.setIntervalSteps(trajSteps)
            boolean initVelocities = true
            molDynOpenMM.dynamic(dynamics.steps, dynamics.dt, dynamics.report, dynamics.write, dynamics.temp, initVelocities, dyn)
        } else {
            logger.severe(" Could not start OpenMM molecular dynamics.")
        }

        return this
    }
}

/**
 * Title: Force Field X.
 *
 * Description: Force Field X - Software for Molecular Biophysics.
 *
 * Copyright: Copyright (c) Michael J. Schnieders 2001-2018.
 *
 * This file is part of Force Field X.
 *
 * Force Field X is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * Force Field X is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Force Field X; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
