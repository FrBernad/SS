import os

if __name__ == "__main__":
    os.system("java -jar ParticlesGenerator.jar -DstaticFile='path/to/static/file' -DdynamicFile='path/to/dynamic/file' -DresultsFile='path/to/results/file' -DexitTimeFile='path/to/exitTime/file' -Ddt=dt -Ddt2=dt2 -Dtf=tf -DD=D -DW=W -DL=L -DA=A -Dw=w -Dm=m -Dr0=r0 -Ddr=dr -DN=N -Dkn=kn -Dkt=kt -Dg=g")